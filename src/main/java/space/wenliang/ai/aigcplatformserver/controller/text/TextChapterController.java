package space.wenliang.ai.aigcplatformserver.controller.text;

import com.alibaba.fastjson2.JSON;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.ChapterParse;
import space.wenliang.ai.aigcplatformserver.bean.text.*;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ChapterService;
import space.wenliang.ai.aigcplatformserver.service.PathService;
import space.wenliang.ai.aigcplatformserver.service.ProjectService;
import space.wenliang.ai.aigcplatformserver.utils.AudioUtils;
import space.wenliang.ai.aigcplatformserver.utils.ChapterUtil;
import space.wenliang.ai.aigcplatformserver.utils.ForEach;
import space.wenliang.ai.aigcplatformserver.utils.SubtitleUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("text/chapter")
public class TextChapterController {

    private final PathConfig pathConfig;

    private final ProjectService projectService;

    private final ChapterService chapterService;
    private final PathService pathService;

    public TextChapterController(PathConfig pathConfig, ProjectService projectService, ChapterService chapterService, PathService pathService) {
        this.pathConfig = pathConfig;
        this.projectService = projectService;
        this.chapterService = chapterService;
        this.pathService = pathService;
    }

    @PostMapping("queryChapters")
    public Result<Object> queryChapters(@RequestBody Chapter vo) throws IOException {
        Path projectPath = pathService.buildProjectPath("text", vo.getProject(), "章节");
        List<Chapter> chapters = new ArrayList<>();
        if (Files.exists(projectPath)) {
            chapters = Files.list(projectPath).map(path -> {
                Chapter chapter = new Chapter();
                chapter.setChapter(path.getFileName().toString());

                try {
                    Files.list(path).forEach(path1 -> {
                        try {
                            if (path1.getFileName().toString().equals("chapterInfo.json")) {
                                List<ChapterInfo> chapterInfos = JSON.parseArray(Files.readString(path1), ChapterInfo.class);
                                chapter.setTextNum(chapterInfos.size());
                            }
                            if (path1.getFileName().toString().equals("aiResult.json")) {
                                chapter.setStage("处理中");
                            }
                            if (path1.getFileName().toString().equals("roles.json")) {
                                List<Role> roles = JSON.parseArray(Files.readString(path1), Role.class);
                                chapter.setRoleNum(roles.size());
                            }
                            if (path1.getFileName().toString().equals("output.wav")) {
                                chapter.setStage("合并完成");
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return chapter;
            }).toList();
        }
        chapters = chapters.stream()
                .sorted(Comparator.comparingInt(s -> Integer.parseInt(s.getChapter().split("--")[0])))
                .toList();
        return Result.success(chapters);
    }

    @PostMapping("tmpChapterSplit")
    public Result<Object> tmpChapterSplit(@RequestBody ChapterSplitVO vo) throws IOException {
        Path originFilePath = pathService.buildProjectPath("text", vo.getProject(), "config", "原文.txt");
        List<ChapterParse> chapterParses = ChapterUtil.chapterSplit(originFilePath.toAbsolutePath().toString(), vo.getChapterPattern());
        List<String> titleList = chapterParses.stream().map(ChapterParse::getTitle).toList();
        return Result.success(titleList);
    }

    @PostMapping("chapterSplit")
    public Result<Object> chapterSplit(@RequestBody ChapterSplitVO vo) throws IOException {
        Path originFilePath = pathService.buildProjectPath("text", vo.getProject(), "config", "原文.txt");
        List<ChapterParse> chapterParses = ChapterUtil.chapterSplit(
                originFilePath.toAbsolutePath().toString(), vo.getChapterPattern());

        for (ChapterParse chapterPars : chapterParses) {
            if (chapterPars.getContent().getBytes(StandardCharsets.UTF_8).length / 1000 > 100) {
                throw new BizException("单章节内容过多会导致程序卡顿甚至爆炸，请合理划分章节！");
            }
        }

        List<String> linesModifiers = new ArrayList<>();
        if (StringUtils.isNotBlank(vo.getLinesPattern())) {
            linesModifiers.add(vo.getLinesPattern());
        }

        ForEach.forEach(chapterParses, (index, chapterParse) -> {
            try {
                List<ChapterInfo> chapterInfos = ChapterUtil.parseChapterInfo(chapterParse.getContent(), linesModifiers);

                Role asideRole = new Role("旁白");
                chapterInfos = chapterInfos.stream().peek(chapterInfo -> chapterInfo.setRoleInfo(asideRole)).toList();

                String chapterDirName = index + "--" + chapterParse.getTitle();
                Path chapterDir = pathService.buildProjectPath("text", vo.getProject(), "章节", chapterDirName);

                Files.createDirectories(chapterDir);

                Path chapterInfoPath = Path.of(chapterDir.toAbsolutePath().toString(), "chapterInfo.json");
                Files.write(chapterInfoPath, JSON.toJSONBytes(chapterInfos));

                Path rolesPath = pathService.buildProjectPath("text", vo.getProject(), "章节", chapterDirName, "roles.json");
                Files.write(rolesPath, JSON.toJSONBytes(List.of(asideRole)));

                Path textPath = Path.of(chapterDir.toAbsolutePath().toString(), "章节原文.txt");
                Files.write(textPath, chapterParse.getContent().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return Result.success();
    }

    @PostMapping("queryChapterText")
    public Result<Object> queryChapterText(@RequestBody Chapter vo) throws IOException {
        Path path = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "章节原文.txt");
        if (Files.exists(path)) {
            return Result.success(Files.readString(path));
        }
        return Result.success();
    }

    @PostMapping("tmpLinesParse")
    public Result<Object> tmpLinesParse(@RequestBody ChapterSplitVO vo) throws IOException {

        if (StringUtils.isNotBlank(vo.getTextContent())) {
            List<String> linesModifiers = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getLinesPattern())) {
                linesModifiers.add(vo.getLinesPattern());
            }
            List<ChapterInfo> chapterInfos = ChapterUtil.parseChapterInfo(vo.getTextContent(), linesModifiers);
            List<String> lines = chapterInfos.stream().filter(ChapterInfo::getLinesFlag).map(ChapterInfo::getText).toList();
            return Result.success(lines);
        }
        return Result.success();
    }

    @PostMapping("linesParse")
    public Result<Object> linesParse(@RequestBody ChapterSplitVO vo) throws IOException {
        if (StringUtils.isNotBlank(vo.getTextContent())) {
            try {

                List<String> linesModifiers = new ArrayList<>();
                if (StringUtils.isNotBlank(vo.getLinesPattern())) {
                    linesModifiers.add(vo.getLinesPattern());
                }
                List<ChapterInfo> chapterInfos = ChapterUtil.parseChapterInfo(vo.getTextContent(),linesModifiers);

                Role asideRole = new Role("旁白");
                chapterInfos = chapterInfos.stream().peek(chapterInfo -> chapterInfo.setRoleInfo(asideRole)).toList();
                Path chapterInfoPath = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "chapterInfo.json");
                Files.write(chapterInfoPath, JSON.toJSONBytes(chapterInfos));

                Path rolesPath = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "roles.json");
                Files.write(rolesPath, JSON.toJSONBytes(List.of(asideRole)));

                Path textPath = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "章节原文.txt");
                Files.write(textPath, vo.getTextContent().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Result.success();
    }

    @PostMapping("queryChapterInfo")
    public Result<Object> queryChapterInfo(@RequestBody Chapter vo) throws IOException {
        List<ChapterInfo> chapterInfos = new ArrayList<>();
        if (StringUtils.isNotBlank(vo.getProject()) && StringUtils.isNotBlank(vo.getChapter())) {
            Path chapterInfoPath = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "chapterInfo.json");
            if (Files.exists(chapterInfoPath)) {
                chapterInfos = JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);
            }

            Path audioDirPath = pathService.buildProjectPath("text", vo.getProject(), "章节", vo.getChapter(), "audio");
            if (Files.exists(audioDirPath)) {
                Map<String, String> audioNameMap = Files.list(audioDirPath)
                        .map(path -> path.getFileName().toString()).collect(Collectors.toMap(
                                (String s) -> {
                                    String[] split = s.split("-");
                                    return split[0] + "-" + split[1];
                                },
                                Function.identity(),
                                (a, b) -> a
                        ));
                for (ChapterInfo chapterInfo : chapterInfos) {
                    String key = chapterInfo.getP() + "-" + chapterInfo.getS();
                    if (audioNameMap.containsKey(key)) {
                        chapterInfo.setAudioUrl(pathConfig.buildProjectUrl("text", vo.getProject(), "章节", vo.getChapter(), "audio", audioNameMap.get(key)));
                    }
                }
            }
        }

        return Result.success(chapterInfos);
    }

    @PostMapping(value = "aiInference")
    public Flux<String> aiInference(@RequestBody Chapter vo) throws IOException {
        return chapterService.linesAnalysis(vo.getProject(), vo.getChapter(), true);
    }

    @PostMapping(value = "checkAiResult")
    public Result<Object> checkAiResult(@RequestBody Chapter vo) throws IOException {
        Path aiResultPath = pathService.getAiResultPath(vo.getProject(), vo.getChapter());
        if (Files.exists(aiResultPath) && StringUtils.isNotBlank(Files.readString(aiResultPath))) {
            return Result.success(true);
        }
        return Result.success(false);
    }

    @PostMapping(value = "loadAiResult")
    public Result<Object> loadAiResult(@RequestBody Chapter vo) throws IOException {
        Path aiResultPath = pathService.getAiResultPath(vo.getProject(), vo.getChapter());
        if (Files.exists(aiResultPath) && StringUtils.isNotBlank(Files.readString(aiResultPath))) {
            String string = Files.readString(aiResultPath);
            chapterService.mergeAiResultInfo(vo.getProject(), vo.getChapter(), string);
        }
        return Result.success();
    }

    @PostMapping(value = "queryRoles")
    public Result<Object> queryRoles(@RequestBody Chapter vo) throws IOException {
        List<Role> roles = new ArrayList<>();
        if (StringUtils.isNotBlank(vo.getProject()) && StringUtils.isNotBlank(vo.getChapter())) {
            roles = chapterService.getRoles(vo.getProject(), vo.getChapter());
        }
        return Result.success(roles);
    }

    @PostMapping(value = "queryCommonRoles")
    public Result<Object> queryCommonRoles(@RequestBody Chapter vo) throws IOException {
        List<Role> roles = new ArrayList<>();
        if (StringUtils.isNotBlank(vo.getProject())) {
            roles = projectService.getCommonRoles(vo.getProject());
        }
        return Result.success(roles);
    }

    @PostMapping(value = "createCommonRole")
    public Result<Object> createCommonRole(@RequestBody RoleModelChange roleModelChange) throws IOException {
        projectService.createCommonRole(roleModelChange);
        return Result.success();
    }

    @PostMapping(value = "deleteCommonRole")
    public Result<Object> deleteCommonRole(@RequestBody RoleModelChange roleModelChange) throws IOException {
        projectService.deleteCommonRole(roleModelChange);
        return Result.success();
    }

    @PostMapping(value = "textModelChange")
    public Result<Object> textModelChange(@RequestBody TextModelChange textModelChange) throws IOException {
        chapterService.textModelChange(textModelChange);
        return Result.success();
    }

    @PostMapping(value = "roleModelChange")
    public Result<Object> roleModelChange(@RequestBody RoleModelChange roleModelChange) throws IOException {
        chapterService.roleModelChange(roleModelChange);
        return Result.success();
    }


    @PostMapping(value = "commonRoleModelChange")
    public Result<Object> commonRoleModelChange(@RequestBody RoleModelChange roleModelChange) throws IOException {
        chapterService.commonRoleModelChange(roleModelChange);
        return Result.success();
    }

    @PostMapping(value = "textRoleChange")
    public Result<Object> textRoleChange(@RequestBody TextModelChange textModelChange) throws IOException {
        chapterService.textRoleChange(textModelChange);
        return Result.success();
    }

    @PostMapping(value = "roleRename")
    public Result<Object> roleRename(@RequestBody RoleRename roleRename) throws IOException {
        chapterService.roleRename(roleRename);
        return Result.success();
    }

    @PostMapping(value = "roleCombine")
    public Result<Object> roleCombine(@RequestBody RoleRename roleRename) throws IOException {
        chapterService.roleCombine(roleRename);
        return Result.success();
    }

    @PostMapping(value = "updateChapterText")
    public Result<Object> updateChapterText(@RequestBody ChapterInfoParam param) throws IOException {
        chapterService.updateChapterText(param);
        return Result.success();
    }

    @PostMapping(value = "startCreateAudio")
    public Result<List<String>> startCreateAudio(@RequestBody AudioCreateParam param) throws IOException {
        Tuple2<Integer, List<String>> tuple2 = chapterService.startCreateAudio(param);
        return Result.success(tuple2._2).setMsg("提交任务数：" + tuple2._1);
    }

    @PostMapping(value = "createAudio")
    public Result<List<String>> createAudio(@RequestBody ChapterInfoParam param) throws Exception {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();
        List<String> creatingIds = chapterService.createAudio(chapter.getProject(), chapter.getChapter(), chapterInfo);
        return Result.success(creatingIds).setMsg("提交任务数：1");
    }

    @PostMapping(value = "stopCreateAudio")
    public Result<Object> stopCreateAudio() throws Exception {
        chapterService.stopCreateAudio();
        return Result.success();
    }

    @PostMapping(value = "updateVolume")
    public Result<Object> updateVolume(@RequestBody ChapterInfoParam param) throws IOException {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();
        List<ChapterInfo> chapterInfos = chapterService.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo item : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), item.getP())
                    && Objects.equals(chapterInfo.getS(), item.getS())) {
                item.setVolume(chapterInfo.getVolume());
                item.setModified();
            }
        }
        chapterService.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
        return Result.success();
    }

    @PostMapping(value = "updateSpeed")
    public Result<Object> updateSpeed(@RequestBody ChapterInfoParam param) throws IOException {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();
        List<ChapterInfo> chapterInfos = chapterService.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo item : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), item.getP())
                    && Objects.equals(chapterInfo.getS(), item.getS())) {
                item.setSpeed(chapterInfo.getSpeed());
                item.setModified();
            }
        }
        chapterService.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
        return Result.success();
    }

    @PostMapping(value = "updateInterval")
    public Result<Object> updateInterval(@RequestBody ChapterInfoParam param) throws IOException {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();
        List<ChapterInfo> chapterInfos = chapterService.getChapterInfos(chapter.getProject(), chapter.getChapter());
        for (ChapterInfo item : chapterInfos) {
            if (Objects.equals(chapterInfo.getP(), item.getP())
                    && Objects.equals(chapterInfo.getS(), item.getS())) {
                item.setInterval(chapterInfo.getInterval());
                item.setModified();
            }
        }
        chapterService.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);
        return Result.success();
    }

    @PostMapping(value = "updateControls")
    public Result<Object> updateControls(@RequestBody ControlsUpdateVO vo) throws IOException {
        List<ChapterInfo> chapterInfos = chapterService.getChapterInfos(vo.getProject(), vo.getChapter());
        for (ChapterInfo item : chapterInfos) {
            if (Objects.equals(vo.getEnableVolume(), Boolean.TRUE)) {
                item.setVolume(vo.getVolume());
                item.setModified();
            }
            if (Objects.equals(vo.getEnableSpeed(), Boolean.TRUE)) {
                item.setSpeed(vo.getSpeed());
                item.setModified();
            }
            if (Objects.equals(vo.getEnableInterval(), Boolean.TRUE)) {
                item.setInterval(vo.getInterval());
                item.setModified();
            }
        }
        chapterService.saveChapterInfos(vo.getProject(), vo.getChapter(), chapterInfos);
        return Result.success();
    }

    @PostMapping(value = "chapterExpose")
    public Result<Object> chapterExpose(@RequestBody ChapterExpose chapterExpose) throws IOException {
        Chapter chapter = chapterExpose.getChapter();
        List<String> indexes = chapterExpose.getIndexes();
        Boolean combineAudio = chapterExpose.getCombineAudio();
        Boolean subtitle = chapterExpose.getSubtitle();

        if (CollectionUtils.isEmpty(indexes)) {
            return Result.success();
        }

        List<ChapterInfo> chapterInfos = chapterService.getChapterInfos(chapter.getProject(), chapter.getChapter());

        if (Objects.equals(combineAudio, Boolean.TRUE)) {
            List<String> dirs = List.of("text", chapter.getProject(), "章节", chapter.getChapter(), "audio");
            Path outputDir = pathService.buildProjectPath(dirs.toArray(new String[0]));

            Map<String, String> fileNameMap = new HashMap<>();
            if (Files.exists(outputDir)) {
                fileNameMap = Files.list(outputDir).map(file -> file.getFileName().toString())
                        .filter(s -> s.endsWith(".wav"))
                        .filter(s -> s.split("-").length > 2)
                        .collect(Collectors.toMap((String fileName) -> fileName.split("-")[0] + "-" + fileName.split("-")[1],
                                Function.identity(), (oldValue, newValue) -> newValue));
            }


            List<ChapterInfo> handleList = new ArrayList<>();
            for (ChapterInfo item : chapterInfos) {
                String key = item.getP() + "-" + item.getS();
                if (indexes.contains(key) && StringUtils.isNotBlank(item.getModelType())) {
                    item.setExport(true);
                    if (fileNameMap.containsKey(key)) {
                        List<String> itemDirs = new ArrayList<>(dirs);
                        itemDirs.add(fileNameMap.get(key));
                        item.setAudioPath(pathService.buildProjectPath(itemDirs.toArray(new String[0])).toAbsolutePath().toString());
                        handleList.add(item);
                    }
                } else {
                    item.setExport(false);
                }
            }

            Path outputWavPath = pathService.buildProjectPath("text", chapter.getProject(), "章节", chapter.getChapter(), "output.wav");
            AudioUtils.mergeAudioFiles(handleList, outputWavPath.toAbsolutePath().toString());

            Map<String, ChapterInfo> collect = handleList.stream()
                    .collect(Collectors.toMap(v -> v.getP() + "-" + v.getS(), Function.identity()));

            for (ChapterInfo chapterInfo : chapterInfos) {
                String key = chapterInfo.getP() + "-" + chapterInfo.getS();
                if (collect.containsKey(key)) {
                    chapterInfo.setLengthInMs(collect.get(key).getLengthInMs());
                }
            }

            chapterService.saveChapterInfos(chapter.getProject(), chapter.getChapter(), chapterInfos);

            Path archiveWavPath = pathService.buildProjectPath("text", chapter.getProject(), "output", chapter.getChapter() + ".wav");
            if (Files.notExists(archiveWavPath.getParent())) {
                Files.createDirectories(archiveWavPath.getParent());
            }
            Files.copy(outputWavPath, archiveWavPath);
        }

        if (Objects.equals(subtitle, Boolean.TRUE)) {
            List<ChapterInfo> handleList = chapterInfos.stream().filter(c -> Objects.equals(Boolean.TRUE, c.getExport())).toList();
            Path outputSrtPath = pathService.buildProjectPath("text", chapter.getProject(), "章节", chapter.getChapter(), "output.srt");
            SubtitleUtil.srtFile(handleList, outputSrtPath);

            Path archiveSrtPath = pathService.buildProjectPath("text", chapter.getProject(), "output", chapter.getChapter() + ".srt");
            Files.copy(outputSrtPath, archiveSrtPath);
        }

        return Result.success();
    }
}
