package space.wenliang.ai.aigcplatformserver.controller.text;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import scala.annotation.meta.param;
import space.wenliang.ai.aigcplatformserver.bean.ChapterParse;
import space.wenliang.ai.aigcplatformserver.bean.text.*;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ChapterService;
import space.wenliang.ai.aigcplatformserver.service.ProjectService;
import space.wenliang.ai.aigcplatformserver.utils.ChapterUtil;
import space.wenliang.ai.aigcplatformserver.utils.ForEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("text/chapter")
public class TextChapterController {

    private final PathConfig pathConfig;

    private final ProjectService projectService;

    private final ChapterService chapterService;

    public TextChapterController(PathConfig pathConfig, ProjectService projectService, ChapterService chapterService) {
        this.pathConfig = pathConfig;
        this.projectService = projectService;
        this.chapterService = chapterService;
    }

    @PostMapping("queryChapters")
    public Result<Object> queryChapters(@RequestBody Chapter vo) throws IOException {
        List<String> chapters = new ArrayList<>();
        Path projectPath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "章节");
        if (Files.exists(projectPath)) {
            chapters = Files.list(projectPath).map(path -> path.getFileName().toString()).toList();
        }
        List<String> sortList = chapters.stream()
                .sorted(Comparator.comparingInt(s -> Integer.parseInt(s.split("--")[0])))
                .toList();
        return Result.success(sortList);
    }

    @PostMapping("tmpChapterSplit")
    public Result<Object> tmpChapterSplit(@RequestBody ChapterSplitVO vo) throws IOException {
        Path originFilePath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "config", "原文.txt");
        List<ChapterParse> chapterParses = ChapterUtil.chapterSplit(originFilePath.toAbsolutePath().toString(), vo.getChapterPattern());
        List<String> titleList = chapterParses.stream().map(ChapterParse::getTitle).toList();
        return Result.success(titleList);
    }

    @PostMapping("chapterSplit")
    public Result<Object> chapterSplit(@RequestBody ChapterSplitVO vo) throws IOException {
        Path originFilePath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "config", "原文.txt");
        List<ChapterParse> chapterParses = ChapterUtil.chapterSplit(
                originFilePath.toAbsolutePath().toString(), vo.getChapterPattern());

        for (ChapterParse chapterPars : chapterParses) {
            if (chapterPars.getContent().getBytes(StandardCharsets.UTF_8).length / 1000 > 100) {
                throw new BizException("单章节内容过多会导致程序卡顿甚至爆炸，请合理划分章节！");
            }
        }

        ForEach.forEach(chapterParses, (index, chapterParse) -> {
            try {
                List<ChapterInfo> chapterInfos = ChapterUtil.parseChapterInfo(chapterParse.getContent(), List.of(vo.getLinesPattern()));
                chapterInfos = chapterInfos.stream().peek(chapterInfo -> chapterInfo.setRoleInfo(new Role("旁白"))).toList();

                String chapterDirName = index + "--" + chapterParse.getTitle();
                Path chapterDir = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "章节", chapterDirName);

                Files.createDirectories(chapterDir);

                Path chapterInfoPath = Path.of(chapterDir.toAbsolutePath().toString(), "chapterInfo.json");
                Files.write(chapterInfoPath, JSON.toJSONBytes(chapterInfos));

                Path textPath = Path.of(chapterDir.toAbsolutePath().toString(), "章节原文.txt");
                Files.write(textPath, chapterParse.getContent().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return Result.success();
    }

    @PostMapping("queryChapterInfo")
    public Result<Object> queryChapterInfo(@RequestBody Chapter vo) throws IOException {
        List<ChapterInfo> chapterInfos = new ArrayList<>();
        if (StringUtils.isNotBlank(vo.getProject()) && StringUtils.isNotBlank(vo.getChapter())) {
            Path chapterInfoPath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "章节", vo.getChapter(), "chapterInfo.json");
            if (Files.exists(chapterInfoPath)) {
                chapterInfos = JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);
            }

            Path audioDirPath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "章节", vo.getChapter(), "audio");
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
                        chapterInfo.setAudioUrl(pathConfig.buildFsUrl("text", vo.getProject(), "章节", vo.getChapter(), "audio", audioNameMap.get(key)));
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(vo.getIndexes())) {
            chapterInfos = chapterInfos.stream()
                    .filter(chapterInfo -> vo.getIndexes().contains(chapterInfo.getIndex()))
                    .toList();
        }

        return Result.success(chapterInfos);
    }

    @PostMapping("linesParse")
    public Result<Object> linesParse(@RequestBody Chapter vo) throws IOException {
        if (StringUtils.isNotBlank(vo.getProject()) && StringUtils.isNotBlank(vo.getChapter())) {
            Path chapterInfoPath = Path.of(pathConfig.getFsDir(), "text", vo.getProject(), "章节", vo.getChapter(), "chapterInfo.json");
            if (Files.exists(chapterInfoPath)) {
                List<ChapterInfo> chapterInfos = JSON.parseArray(Files.readString(chapterInfoPath), ChapterInfo.class);
                Map<Integer, List<ChapterInfo>> chapterInfoMap = chapterInfos.stream().collect(Collectors.groupingBy(ChapterInfo::getP));

                List<ChapterInfo> save = new ArrayList<>();
                for (Map.Entry<Integer, List<ChapterInfo>> entry : chapterInfoMap.entrySet()) {
                    StringBuilder pText = new StringBuilder();
                    entry.getValue().stream().sorted(Comparator.comparingInt(ChapterInfo::getS))
                            .map(ChapterInfo::getText)
                            .forEach(pText::append);
                    List<ChapterInfo> saveItem = ChapterUtil.parseLineInfo(entry.getKey(), pText.toString(), List.of("“”"));
                    saveItem = saveItem.stream().peek(c -> c.setRoleInfo(new Role("未知"))).toList();
                    save.addAll(saveItem);
                }
                Files.write(chapterInfoPath, JSON.toJSONBytes(save));
            }
        }

        return Result.success();
    }

    @PostMapping(value = "aiInference")
    public Flux<String> aiInference(@RequestBody Chapter vo) throws IOException {
        return chapterService.linesAnalysis(vo.getProject(), vo.getChapter(), true);
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
    public Result<Object> startCreateAudio(@RequestBody AudioCreateParam param) throws IOException {
        CompletableFuture.runAsync(() -> chapterService.startCreateAudio(param))
                .exceptionally(e -> {
                    log.error(e.getMessage(), e);
                    return null;
                });
        return Result.success("正在生成中");
    }

    @PostMapping(value = "createAudio")
    public Result<Object> createAudio(@RequestBody ChapterInfoParam param) throws Exception {
        Chapter chapter = param.getChapter();
        ChapterInfo chapterInfo = param.getChapterInfo();
        chapterService.createAudio(chapter.getProject(), chapter.getChapter(), chapterInfo);
        return Result.success(chapterInfo);
    }
}
