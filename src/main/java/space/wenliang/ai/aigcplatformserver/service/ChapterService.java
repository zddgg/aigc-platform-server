package space.wenliang.ai.aigcplatformserver.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import space.wenliang.ai.aigcplatformserver.bean.text.*;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioCreater;
import space.wenliang.ai.aigcplatformserver.model.chat.AiService;
import space.wenliang.ai.aigcplatformserver.socket.AudioProcessWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.utils.PathWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChapterService {

    private final AiService aiService;
    private final PathConfig pathConfig;
    private final PathService pathService;
    private final ProjectService projectService;
    private final AudioCreater audioCreater;
    private final AudioProcessWebSocketHandler audioProcessWebSocketHandler;

    public ChapterService(AiService aiService,
                          PathConfig pathConfig,
                          PathService pathService,
                          ProjectService projectService,
                          AudioCreater audioCreater,
                          AudioProcessWebSocketHandler audioProcessWebSocketHandler, ConfigService configService) {
        this.aiService = aiService;
        this.pathConfig = pathConfig;
        this.pathService = pathService;
        this.projectService = projectService;
        this.audioCreater = audioCreater;
        this.audioProcessWebSocketHandler = audioProcessWebSocketHandler;
    }

    @PostConstruct
    public void init() {
        audioCreateTask();
    }

    public List<ChapterInfo> getChapterInfos(String project, String chapter) throws IOException {
        Path chapterInfoPath = pathService.getChapterInfoPath(project, chapter);
        if (Files.exists(chapterInfoPath)) {
            return JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);
        }
        return new ArrayList<>();
    }

    public void saveChapterInfos(String project, String chapter, List<ChapterInfo> chapterInfos) throws IOException {
        Path chapterInfoPath = pathService.getChapterInfoPath(project, chapter);
        if (Files.exists(chapterInfoPath)) {
            Files.write(chapterInfoPath, JSON.toJSONBytes(chapterInfos));
        }
    }

    public Flux<String> linesAnalysis(String project, String chapter, Boolean saveResult) throws IOException {
        Path chapterInfoPath = pathService.getChapterInfoPath(project, chapter);
        if (Files.notExists(chapterInfoPath)) {
            return Flux.empty();
        }

        List<ChapterInfo> chapterInfos = JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);

        Path aiResultJsonPath = pathService.getAiResultPath(project, chapter);

        boolean hasLines = false;

        for (ChapterInfo lineInfo : chapterInfos) {
            if (Objects.equals(lineInfo.getLinesFlag(), Boolean.TRUE)) {
                hasLines = true;
                break;
            }
        }

        if (!hasLines) {
            mergeAiResultInfo(project, chapter, "{}");
            return Flux.empty();
        }

        // 不是重新生成时清空文件
        if (saveResult && Files.exists(aiResultJsonPath)) {
            Files.write(aiResultJsonPath, new byte[0], StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        }

        StringBuilder aiResultStr = new StringBuilder();
        return this.roleAndLinesInference(chapterInfos)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(v -> {
                    System.out.println(v);
                    aiResultStr.append(v);
                })
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.Unauthorized) {
                        return Flux.just("ai api 接口认证异常，请检查api key, " + e.getMessage() + " error");
                    }
                    return Flux.just(e.getMessage() + " error");
                })
                .doOnComplete(() -> {
                    mergeAiResultInfo(project, chapter, aiResultStr.toString());
                });
    }

    public Flux<String> roleAndLinesInference(List<ChapterInfo> chapterInfos) {

        List<JSONObject> linesList = new ArrayList<>();
        chapterInfos.forEach(lineInfo -> {
            if (Objects.equals(lineInfo.getLinesFlag(), Boolean.TRUE)) {
                JSONObject lines = new JSONObject();
                lines.put("index", lineInfo.getP() + "-" + lineInfo.getS());
                lines.put("lines", lineInfo.getText());
                linesList.add(lines);
            }
        });

        String lines = JSON.toJSONString(linesList);
        StringBuilder content = new StringBuilder();

        chapterInfos.stream()
                .collect(Collectors.groupingBy(ChapterInfo::getP, TreeMap::new, Collectors.toList()))
                .values()
                .forEach(val -> {
                    val.stream().sorted(Comparator.comparingInt(ChapterInfo::getS))
                            .map(ChapterInfo::getText)
                            .forEach(content::append);
                    content.append("\n");
                });


        String systemMessage = "你是一个小说内容台词分析员，你会精确的找到台词在原文中的位置并分析属于哪个角色，以及角色在说这句台词时的上下文环境及情绪等。";

        String userMessage = STR."""
                严格按照以下要求工作：
                \{parseStep}

                输出格式如下：
                \{outputFormat}

                台词部分：
                \{lines}

                原文部分：
                \{content.toString()}
                """;

        log.info("提示词, systemMessage: {}", systemMessage);
        log.info("提示词, userMessage: {}", userMessage);

        return aiService.stream(systemMessage, userMessage);
    }

    public void mergeAiResultInfo(String project, String chapter, String aiResultStr) {
        Path chapterInfoPath = pathService.getChapterInfoPath(project, chapter);

        List<Role> commonRoles = projectService.getCommonRoles(project);
        Map<String, Role> commonRoleMap = commonRoles.
                stream()
                .collect(Collectors.toMap(Role::getRole, Function.identity(), (a, b) -> a));

        try {
            List<ChapterInfo> chapterInfos = JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);
            AiResult aiResult = parseAiResult(aiResultStr);

            aiResult = reCombineAiResult(aiResult);

            Path aiResultPath = pathService.getAiResultPath(project, chapter);
            Files.write(aiResultPath, JSON.toJSONBytes(aiResult));

            List<Role> roles = aiResult.getRoles();
            Map<String, Role> roleMap = roles.stream()
                    .collect(Collectors.toMap(Role::getRole, Function.identity(), (a, b) -> a));

            Map<String, LinesMapping> linesMappingMap = aiResult.getLinesMappings().stream()
                    .collect(Collectors.toMap(LinesMapping::getLinesIndex, Function.identity(), (a, b) -> a));
            boolean hasAside = false;
            for (ChapterInfo chapterInfo : chapterInfos) {
                String key = chapterInfo.getP() + "-" + chapterInfo.getS();
                String role = "旁白";
                if (linesMappingMap.containsKey(key)) {
                    LinesMapping linesMapping = linesMappingMap.get(key);
                    role = linesMapping.getRole();
                } else {
                    hasAside = true;
                }
                if (commonRoleMap.containsKey(role)) {
                    chapterInfo.setModelConfig(commonRoleMap.get(role));
                }
                chapterInfo.setRoleInfo(roleMap.get(role));
            }

            Path rolesPath = pathService.getRolesPath(project, chapter);
            if (hasAside) {
                String role = "旁白";
                roles.add(new Role(role));
            }

            for (Role role : roles) {
                if (commonRoleMap.containsKey(role.getRole())) {
                    role.setModelConfig(commonRoleMap.get(role.getRole()));
                }
            }

            Files.write(rolesPath, JSON.toJSONBytes(roles));

            Files.write(chapterInfoPath, JSON.toJSONBytes(chapterInfos));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AiResult parseAiResult(String text) {
        if (text.startsWith("```json") || text.endsWith("```")) {
            text = text.replace("```json", "").replace("```", "");
        }
        return JSON.parseObject(text, AiResult.class);
    }

    public AiResult reCombineAiResult(AiResult aiResult) throws IOException {

        List<Role> roles = aiResult.getRoles();
        List<LinesMapping> linesMappings = aiResult.getLinesMappings();

        // 大模型总结的角色列表有时候会多也会少
        List<Role> combineRoles = combineRoles(roles, linesMappings);

        AiResult result = new AiResult();
        result.setLinesMappings(linesMappings);
        result.setRoles(combineRoles);
        return result;
    }

    public List<Role> combineRoles(List<Role> roles, List<LinesMapping> linesMappings) {
        Map<String, Long> linesRoleCountMap = linesMappings.stream()
                .collect(Collectors.groupingBy(LinesMapping::getRole, Collectors.counting()));
        List<Role> filterRoles = roles.stream().filter(r -> linesRoleCountMap.containsKey(r.getRole())).toList();

        Set<String> filterRoleSet = filterRoles.stream().map(Role::getRole).collect(Collectors.toSet());
        List<Role> newRoleList = linesMappings.stream().filter(m -> !filterRoleSet.contains(m.getRole()))
                .map(m -> {
                    Role role = new Role();
                    role.setRole(m.getRole());
                    role.setGender(m.getGender());
                    role.setAgeGroup(m.getAgeGroup());
                    return role;
                })
                .collect(Collectors.toMap(Role::getRole, Function.identity(), (v1, v2) -> v1))
                .values().stream().toList();

        List<Role> newRoles = new ArrayList<>();
        newRoles.addAll(filterRoles);
        newRoles.addAll(newRoleList);
        newRoles.sort(Comparator.comparingLong((Role r) -> linesRoleCountMap.get(r.getRole())).reversed());
        return newRoles;
    }

    public List<Role> getRoles(String project, String chapter) throws IOException {
        Path rolesPath = pathService.getRolesPath(project, chapter);
        if (Files.exists(rolesPath)) {
            List<Role> roles = JSON.parseArray(Files.readString(rolesPath), Role.class);

            List<Role> commonRoles = projectService.getCommonRoles(project);
            List<ChapterInfo> chapterInfos = this.getChapterInfos(project, chapter);
            Map<String, Long> roleCountMap = chapterInfos.stream()
                    .collect(Collectors.groupingBy(ChapterInfo::getRole, Collectors.counting()));

            List<Role> result = new ArrayList<>(roles);

            result = result.stream()
                    .peek(role -> {
                        role.setRoleCount(roleCountMap.getOrDefault(role.getRole(), 0L).intValue());
                    })
                    .sorted(Comparator.comparingInt((Role role) -> {
                        List<String> roleNames = commonRoles.stream().map(Role::getRole).toList();
                        int index = roleNames.indexOf(role.getRole());
                        return index < 0 ? Integer.MAX_VALUE : index;
                    }))
                    .toList();

            return result;
        }
        return new ArrayList<>();
    }

    public void saveRoles(String project, String chapter, List<Role> roles) throws IOException {
        Path rolesPath = pathService.getRolesPath(project, chapter);
        if (Files.notExists(rolesPath.getParent())) {
            Files.createDirectories(rolesPath.getParent());
        }
        Files.write(rolesPath, JSON.toJSONBytes(roles));
    }

    static String parseStep = """
            1. 分析下面原文中有哪些角色，角色中有观众、群众之类的角色时统一使用观众这个角色，他们的性别和年龄段只能在下面范围中选择一个：
            性别：男、女、未知。
            年龄段：少年、青年、中年、老年、未知。

            2. 请分析下面台词部分的内容是属于原文部分中哪个角色的，然后结合上下文分析当时的情绪，情绪只能在下面范围中选择一个：
            情绪：中立、开心、吃惊、难过、厌恶、生气、恐惧。

            3. 严格按照台词文本中的顺序在原文文本中查找。每行台词都做一次处理，不能合并台词。
            4. 返回结果是JSON数组结构的字符串。
            5. 分析的台词内容如果不是台词，不要加入到返回结果中。
            """;

    static String outputFormat = """
            {
              "roles": [
                {
                  "role": "这里是具体的角色名",
                  "gender": "男",
                  "ageGroup": "青少年"
                }
              ],
              "linesMappings": [
                {
                  "linesIndex": "这里的值是台词前的序号",
                  "role": "这里是具体的角色名",
                  "gender": "男",
                  "ageGroup": "青少年",
                  "mood": "自卑"
                }
              ]
            }
            """;


    public void textModelChange(TextModelChange textModelChange) throws IOException {
        Chapter chapter = textModelChange.getChapter();
        ChapterInfo newChapterInfo = textModelChange.getChapterInfo();

        List<ChapterInfo> chapterInfos = this.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo chapterInfo : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), newChapterInfo.getP())
                    && Objects.equals(chapterInfo.getS(), newChapterInfo.getS())) {
                chapterInfo.setModelConfig(newChapterInfo);
            }
        }
        this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
    }

    public void roleModelChange(RoleModelChange roleModelChange) throws IOException {
        Chapter chapter = roleModelChange.getChapter();
        Role newRole = roleModelChange.getRole();
        List<Role> roles = this.getRoles(chapter.getProject(), chapter.getChapter());
        for (Role role : roles) {
            if (StringUtils.equals(newRole.getRole(), role.getRole())) {
                role.setModelConfig(newRole);
            }
        }
        this.saveRoles(chapter.getProject(), chapter.getChapter(), roles);

        List<ChapterInfo> chapterInfos = this.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo chapterInfo : chapterInfos) {
            if (StringUtils.equals(newRole.getRole(), chapterInfo.getRole())) {
                chapterInfo.setModelConfig(newRole);
            }
        }
        this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
    }

    public void textRoleChange(TextModelChange textModelChange) throws IOException {
        Chapter chapter = textModelChange.getChapter();
        ChapterInfo newChapterInfo = textModelChange.getChapterInfo();
        Boolean loadModel = textModelChange.getLoadModel();

        List<Role> roles = getRoles(chapter.getProject(), chapter.getChapter());
        Optional<Role> optional = roles.stream().filter(r -> StringUtils.equals(r.getRole(), newChapterInfo.getRole())).findAny();
        if (optional.isEmpty()) {
            loadModel = true;
            List<Role> newRoles = new ArrayList<>(roles);
            newRoles.add(newChapterInfo);
            saveRoles(chapter.getProject(), chapter.getChapter(), newRoles);
        }

        List<ChapterInfo> chapterInfos = this.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo chapterInfo : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), newChapterInfo.getP())
                    && Objects.equals(chapterInfo.getS(), newChapterInfo.getS())) {
                chapterInfo.setRoleInfo(newChapterInfo);
                if (Objects.equals(loadModel, Boolean.TRUE)) {
                    chapterInfo.setModelConfig(newChapterInfo);
                }
                if (StringUtils.equals(newChapterInfo.getRole(), "旁白")) {
                    chapterInfo.setLinesFlag(false);
                }
            }
        }

        this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
    }

    public void roleRename(RoleRename roleRename) throws IOException {
        Chapter chapter = roleRename.getChapter();
        if (StringUtils.equals(roleRename.getRoleType(), "role")) {
            List<Role> roles = this.getRoles(chapter.getProject(), chapter.getChapter());
            Optional<Role> exist = roles.stream()
                    .filter(r -> StringUtils.equals(r.getRole(), roleRename.getNewRole())).findAny();

            if (exist.isPresent()) {
                throw new BizException("已存在角色，请使用删除合并");
            }

            for (Role role : roles) {
                if (StringUtils.equals(role.getRole(), roleRename.getRole())) {
                    role.setRole(roleRename.getNewRole());
                }
            }

            this.saveRoles(chapter.getProject(), chapter.getChapter(), roles);

            List<ChapterInfo> chapterInfos = getChapterInfos(chapter.getProject(), chapter.getChapter());
            for (ChapterInfo chapterInfo : chapterInfos) {
                if (StringUtils.equals(chapterInfo.getRole(), roleRename.getRole())) {
                    chapterInfo.setRole(roleRename.getNewRole());
                    if (StringUtils.equals(roleRename.getNewRole(), "旁白")) {
                        chapterInfo.setLinesFlag(false);
                    }
                }
            }

            this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
        }

        if (StringUtils.equals(roleRename.getRoleType(), "commonRole")) {
            List<Role> commonRoles = projectService.getCommonRoles(chapter.getProject());
            for (Role role : commonRoles) {
                if (StringUtils.equals(role.getRole(), roleRename.getRole())) {
                    role.setRole(roleRename.getNewRole());
                }
            }
            projectService.saveCommonRoles(chapter.getProject(), commonRoles);
        }

    }

    public void roleCombine(RoleRename roleRename) throws IOException {
        Chapter chapter = roleRename.getChapter();
        List<Role> roles = this.getRoles(chapter.getProject(), chapter.getChapter());

        Optional<Role> exist = roles.stream()
                .filter(r -> StringUtils.equals(r.getRole(), roleRename.getNewRole())).findAny();

        if (exist.isPresent()) {
            List<Role> saveRoles = roles.stream().filter(r -> !StringUtils.equals(r.getRole(), roleRename.getRole())).toList();

            this.saveRoles(chapter.getProject(), chapter.getChapter(), saveRoles);

            List<ChapterInfo> chapterInfos = this.getChapterInfos(chapter.getProject(), chapter.getChapter());
            for (ChapterInfo chapterInfo : chapterInfos) {
                if (StringUtils.equals(chapterInfo.getRole(), roleRename.getRole())) {
                    chapterInfo.setRoleInfo(exist.get());
                    chapterInfo.setModelConfig(exist.get());
                    if (StringUtils.equals(roleRename.getNewRole(), "旁白")) {
                        chapterInfo.setLinesFlag(false);
                    }
                }
            }

            this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
        }
    }

    public void commonRoleModelChange(RoleModelChange roleModelChange) {
        Chapter chapter = roleModelChange.getChapter();
        Role role = roleModelChange.getRole();
        List<Role> commonRoles = projectService.getCommonRoles(chapter.getProject());
        for (Role commonRole : commonRoles) {
            if (StringUtils.equals(commonRole.getRole(), role.getRole())) {
                commonRole.setModelConfig(role);
            }
        }
        projectService.saveCommonRoles(chapter.getProject(), commonRoles);
    }

    public void updateChapterText(ChapterInfoParam param) throws IOException {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();

        List<ChapterInfo> chapterInfos = this.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo info : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), info.getP())
                    && Objects.equals(chapterInfo.getS(), info.getS())) {
                info.setText(chapterInfo.getText());
                info.setModified();
            }
        }

        this.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
    }

    public void stopCreateAudio() {
        audioCreateTaskQueue.clear();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AudioCreateTask {
        private String project;
        private String chapter;
        private ChapterInfo chapterInfo;
    }

    public static final LinkedBlockingDeque<AudioCreateTask> audioCreateTaskQueue = new LinkedBlockingDeque<>();

    public int startCreateAudio(AudioCreateParam param) {
        try {
            List<ChapterInfo> chapterInfos = this.getChapterInfos(param.getProject(), param.getChapter())
                    .stream().filter(c -> StringUtils.isNotBlank(c.getModelType())).toList();
            if (StringUtils.equals(param.getActionType(), "modified")) {
                chapterInfos = chapterInfos.stream()
                        .filter(c -> c.getAudioStage() == ChapterInfo.modified)
                        .toList();
            }

            // 检查modalType server config

            log.info("批量生成数量，{}", chapterInfos.size());

            for (ChapterInfo chapterInfo : chapterInfos) {
                audioCreateTaskQueue.add(new AudioCreateTask(param.getProject(), param.getChapter(), chapterInfo));
            }

            log.info("批量生成任务提交成功");
            return chapterInfos.size();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createAudio(String project, String chapter, ChapterInfo chapterInfo) throws Exception {
        audioCreateTaskQueue.add(new AudioCreateTask(project, chapter, chapterInfo));
    }

    public void audioCreateTask() {
        CompletableFuture.runAsync(() -> {
                    while (true) {
                        try {
                            AudioCreateTask audioCreateTask = audioCreateTaskQueue.takeFirst();
                            createAudio(audioCreateTask);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }, Executors.newFixedThreadPool(1))
                .exceptionally(e -> {
                    log.error(e.getMessage(), e);
                    return null;
                });
    }

    public void createAudio(AudioCreateTask audioCreateTask) throws Exception {
        String project = audioCreateTask.getProject();
        String chapter = audioCreateTask.getChapter();
        ChapterInfo chapterInfo = audioCreateTask.getChapterInfo();

        AudioContext audioContext = new AudioContext();
        audioContext.setText(chapterInfo.getText());
        audioContext.setTextLang("zh");
        audioContext.setType(chapterInfo.getModelType());

        Path outputDir = pathService.buildProjectPath("text", project, "章节", chapter, "audio");
        if (Files.notExists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        String fileName = chapterInfo.getP() + "-" + chapterInfo.getS() + "-" + new Date().getTime();
        audioContext.setOutputDir(outputDir.toAbsolutePath().toString());
        audioContext.setOutputName(fileName);


        audioContext.setType(chapterInfo.getModelType());

        if (StringUtils.equals(chapterInfo.getModelType(), "edge-tts")) {
            String[] model = chapterInfo.getModel().toArray(new String[0]);
            audioContext.setSpeaker(model[0]);

        } else if (StringUtils.equals(chapterInfo.getModelType(), "chat-tts")) {
            audioContext.setChatTtsConfig(chapterInfo.getChatTtsConfig());

        } else if (List.of("gpt-sovits", "fish-speech").contains(chapterInfo.getModelType())) {
            String[] array = chapterInfo.getAudio().toArray(new String[0]);

            Path refAudioPath = pathService.buildRmModelPath("ref-audio", array[0], array[1], array[2], array[3]);

            audioContext.setRefAudioPath(PathWrapper.getAbsolutePath(refAudioPath, pathConfig.hasRemotePlatForm()));
            audioContext.setRefText(array[3].replace(".wav", ""));
            audioContext.setRefTextLang("zh");

            String[] model = chapterInfo.getModel().toArray(new String[0]);
            audioContext.setModelGroup(model[0]);
            audioContext.setModel(model[1]);
        }

        log.info(JSON.toJSONString(audioContext));
        audioCreater.createFile(audioContext);

        chapterInfo.setAudioUrl(pathConfig.buildProjectUrl("text", project, "章节", chapter, "audio", fileName + ".wav"));

        List<String> list = new ArrayList<>();
        // 删除之前的音频
        Files.list(outputDir).forEach(path -> {
            if (path.getFileName().toString().startsWith(chapterInfo.getP() + "-" + chapterInfo.getS())
                    && !StringUtils.equals(path.getFileName().toString(), fileName + ".wav")) {
                list.add(path.toAbsolutePath().toString());
            }
        });
        if (!CollectionUtils.isEmpty(list)) {
            for (String s : list) {
                Files.deleteIfExists(Path.of(s));
            }
        }

        List<ChapterInfo> chapterInfos = this.getChapterInfos(project, chapter);
        for (ChapterInfo info : chapterInfos) {
            if (Objects.equals(info.getP(), chapterInfo.getP())
                    && Objects.equals(info.getS(), chapterInfo.getS())) {
                info.setAudioStage(ChapterInfo.created);
                chapterInfo.setAudioStage(ChapterInfo.created);
            }
        }

        this.saveChapterInfos(project, chapter, chapterInfos);

        JSONObject j1 = new JSONObject();
        j1.put("type", "result");
        j1.put("project", project);
        j1.put("chapter", chapter);
        j1.put("chapterInfo", chapterInfo);
        audioProcessWebSocketHandler.sendMessageToProject(project, JSON.toJSONString(j1));
        JSONObject j2 = new JSONObject();
        j2.put("type", "stage");
        j2.put("project", project);
        j2.put("taskNum", audioCreateTaskQueue.size());

        List<String> creatingIds = new ArrayList<>();
        audioCreateTaskQueue.forEach(t -> creatingIds.add(t.getChapterInfo().getIndex()));
        j2.put("creatingIds", creatingIds);
        audioProcessWebSocketHandler.sendMessageToProject(project, JSON.toJSONString(j2));
    }
}
