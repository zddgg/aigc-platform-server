package space.wenliang.ai.aigcplatformserver.service.business.impl;

import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.*;
import space.wenliang.ai.aigcplatformserver.service.business.BTextProjectService;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class BTextProjectServiceImpl implements BTextProjectService {

    private final PathConfig pathConfig;
    private final ATextProjectService aTextProjectService;
    private final ATextChapterService aTextChapterService;
    private final AChapterInfoService aChapterInfoService;
    private final ATextRoleService aTextRoleService;
    private final ATextCommonRoleService aTextCommonRoleService;
    private final ARoleInferenceService aRoleInferenceService;

    public BTextProjectServiceImpl(PathConfig pathConfig,
                                   ATextProjectService aTextProjectService,
                                   ATextChapterService aTextChapterService,
                                   AChapterInfoService aChapterInfoService,
                                   ATextRoleService aTextRoleService,
                                   ATextCommonRoleService aTextCommonRoleService,
                                   ARoleInferenceService aRoleInferenceService) {
        this.pathConfig = pathConfig;
        this.aTextProjectService = aTextProjectService;
        this.aTextChapterService = aTextChapterService;
        this.aChapterInfoService = aChapterInfoService;
        this.aTextRoleService = aTextRoleService;
        this.aTextCommonRoleService = aTextCommonRoleService;
        this.aRoleInferenceService = aRoleInferenceService;
    }

    private static @NotNull List<TextChapterEntity> getTextChapterEntities(String projectId,
                                                                           List<Tuple2<String, String>> tuple2s,
                                                                           String dialoguePattern) {
        List<TextChapterEntity> textChapterEntities = new ArrayList<>();
        int i = 0;
        for (Tuple2<String, String> tuple2 : tuple2s) {
            TextChapterEntity textChapterEntity = new TextChapterEntity();
            textChapterEntity.setProjectId(projectId);
            textChapterEntity.setChapterId(IdUtils.uuid());
            textChapterEntity.setChapterName(tuple2._1);
            textChapterEntity.setContent(tuple2._2);
            textChapterEntity.setDialoguePattern(dialoguePattern);
            textChapterEntity.setSortOrder(i++);

            textChapterEntities.add(textChapterEntity);
        }
        return textChapterEntities;
    }

    @Override
    public List<TextProject> list() {
        List<TextProjectEntity> entityList = aTextProjectService.list();
        Map<String, Long> countMap = aTextChapterService.chapterCount();
        return entityList.stream().map(e -> {
            TextProject textProject = new TextProject();
            BeanUtils.copyProperties(e, textProject);
            textProject.setChapterCount(countMap.get(e.getProjectId()));
            return textProject;
        }).toList();
    }

    @Override
    public void create(String project, String projectType, String content) {

        TextProjectEntity entity = aTextProjectService.getByProjectName(project);
        if (Objects.nonNull(entity)) {
            throw new BizException("已存在[" + project + "]项目");
        }

        TextProjectEntity save = new TextProjectEntity();
        String projectId = IdUtils.uuid();
        save.setProjectId(projectId);
        save.setProjectName(project);
        save.setProjectType(projectType);
        save.setContent(content);
        aTextProjectService.save(save);

        if (StringUtils.equals(projectType, "short_text")) {
            TextChapterEntity textChapterEntity = new TextChapterEntity();
            textChapterEntity.setProjectId(projectId);
            textChapterEntity.setChapterId(IdUtils.uuid());
            textChapterEntity.setChapterName("单章节");
            textChapterEntity.setContent(content);

            aTextChapterService.save(textChapterEntity);
        }
    }

    @Override
    public List<String> tmpChapterSplit(String projectId, String chapterPattern, String dialoguePattern) {
        TextProjectEntity projectEntity = aTextProjectService.getAndContentByProjectId(projectId);
        if (Objects.isNull(projectEntity)) {
            throw new BizException("项目不存在");
        }

        String content = projectEntity.getContent();
        List<Tuple2<String, String>> tuple2s;
        try {
            tuple2s = ChapterUtils.chapterSplit(content.getBytes(), chapterPattern);
        } catch (IOException e) {
            throw new BizException(e.getMessage());
        }

        List<String> chapterTitles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tuple2s)) {
            List<TextChapterEntity> textChapterEntities = getTextChapterEntities(projectId, tuple2s, dialoguePattern);
            chapterTitles = textChapterEntities.stream()
                    .map(TextChapterEntity::getChapterName)
                    .toList();
        }
        return chapterTitles;
    }

    @Override
    public void chapterSplit(String projectId, String chapterPattern, String dialoguePattern) {
        TextProjectEntity projectEntity = aTextProjectService.getAndContentByProjectId(projectId);
        if (Objects.isNull(projectEntity)) {
            throw new BizException("项目不存在");
        }

        String content = projectEntity.getContent();
        List<Tuple2<String, String>> tuple2s;
        try {
            tuple2s = ChapterUtils.chapterSplit(content.getBytes(), chapterPattern);
        } catch (IOException e) {
            throw new BizException(e.getMessage());
        }

        if (!CollectionUtils.isEmpty(tuple2s)) {

            List<TextChapterEntity> textChapterEntities = getTextChapterEntities(projectId, tuple2s, dialoguePattern);

            aTextChapterService.deleteByProjectId(projectId);
            aTextChapterService.saveBatch(textChapterEntities);
        }

        projectEntity.setChapterPattern(chapterPattern);
        aTextProjectService.updateById(projectEntity);
    }

    @Override
    public void update(TextProjectEntity textProjectEntity) {
        TextProjectEntity project = aTextProjectService.getOne(textProjectEntity.getProjectId());
        if (Objects.isNull(project)) {
            throw new BizException("项目不存在");
        }
        project.setProjectName(textProjectEntity.getProjectName());
        aTextProjectService.updateProjectName(textProjectEntity.getProjectId(), textProjectEntity.getProjectName());
    }

    @Override
    public void delete(TextProjectEntity textProjectEntity) throws IOException {
        TextProjectEntity project = aTextProjectService.getOne(textProjectEntity.getProjectId());
        if (Objects.isNull(project)) {
            log.error("text project is null");
        }

        aChapterInfoService.deleteByProjectId(textProjectEntity.getProjectId());
        aTextChapterService.deleteByProjectId(textProjectEntity.getProjectId());
        aTextProjectService.deleteByProjectId(textProjectEntity.getProjectId());
        aTextRoleService.deleteByProjectId(textProjectEntity.getProjectId());
        aTextCommonRoleService.deleteByProjectId(textProjectEntity.getProjectId());
        aRoleInferenceService.deleteByProjectId(textProjectEntity.getProjectId());

        FileUtils.deleteDirectoryAll(Path.of(
                pathConfig.getProjectDir(),
                "text",
                FileUtils.fileNameFormat(textProjectEntity.getProjectName())
        ));
    }
}
