package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.FormatTextProject;
import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.*;
import space.wenliang.ai.aigcplatformserver.service.business.BTextProjectService;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BTextProjectServiceImpl implements BTextProjectService {

    private final EnvConfig envConfig;
    private final TextRoleService textRoleService;
    private final TextProjectService textProjectService;
    private final TextChapterService textChapterService;
    private final ChapterInfoService chapterInfoService;
    private final TextCommonRoleService textCommonRoleService;
    private final TextRoleInferenceService textRoleInferenceService;

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
    public List<TextProject> projectList() {
        List<TextProjectEntity> textProjectEntities = textProjectService.list(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content")));

        Map<String, Integer> countMap = textChapterService.chapterCount();

        return textProjectEntities.stream().map(e -> {
            TextProject textProject = new TextProject();
            BeanUtils.copyProperties(e, textProject);
            textProject.setChapterCount(countMap.get(e.getProjectId()));
            return textProject;
        }).toList();
    }

    @Override
    public TextProjectEntity getByProjectId(String projectId) {
        return textProjectService.getOne(new LambdaQueryWrapper<TextProjectEntity>()
                .select(TextProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(TextProjectEntity::getProjectId, projectId));
    }

    @Override
    public void createProject(String project, String projectType, String content) {

        TextProjectEntity entity = textProjectService.getByProjectName(project);
        if (Objects.nonNull(entity)) {
            throw new BizException("已存在[" + project + "]项目");
        }

        TextProjectEntity save = new TextProjectEntity();
        String projectId = IdUtils.uuid();
        save.setProjectId(projectId);
        save.setProjectName(project);
        save.setProjectType(projectType);
        save.setContent(content);
        textProjectService.save(save);

        if (StringUtils.equals(projectType, "short_text")) {
            TextChapterEntity textChapterEntity = new TextChapterEntity();
            textChapterEntity.setProjectId(projectId);
            textChapterEntity.setChapterId(IdUtils.uuid());
            textChapterEntity.setChapterName("单章节");
            textChapterEntity.setContent(content);

            textChapterService.save(textChapterEntity);
        }
    }

    @Override
    public void createFormatTextProject(FormatTextProject project) {
        String projectName = project.getProjectName();
        String projectType = project.getProjectType();
        List<ChapterInfoEntity> chapterInfos = project.getChapterInfos();

        TextProjectEntity entity = textProjectService.getByProjectName(projectName);
        if (Objects.nonNull(entity)) {
            throw new BizException("已存在[" + project + "]项目");
        }

        String projectId = IdUtils.uuid();
        String chapterId = IdUtils.uuid();

        TextProjectEntity save = new TextProjectEntity();
        save.setProjectId(projectId);
        save.setProjectName(projectName);
        save.setProjectType(projectType);
        textProjectService.save(save);

        TextChapterEntity textChapterEntity = new TextChapterEntity();
        textChapterEntity.setProjectId(projectId);
        textChapterEntity.setChapterId(chapterId);
        textChapterEntity.setChapterName("单章节");

        textChapterService.save(textChapterEntity);

        List<ChapterInfoEntity> saveList = new ArrayList<>();
        Set<String> roles = new HashSet<>();
        for (int i = 0; i < chapterInfos.size(); i++) {
            ChapterInfoEntity item = chapterInfos.get(i);

            ChapterInfoEntity chapterInfo = new ChapterInfoEntity();
            chapterInfo.setProjectId(projectId);
            chapterInfo.setChapterId(chapterId);
            chapterInfo.setParaIndex(i);
            chapterInfo.setSentIndex(0);
            chapterInfo.setText(item.getText());
            chapterInfo.setDialogueFlag(true);
            chapterInfo.setRole(item.getRole());
            chapterInfo.setAudioTaskState(AudioTaskStateConstants.init);

            saveList.add(chapterInfo);
            roles.add(item.getRole());
        }

        chapterInfoService.saveBatch(saveList);

        List<TextRoleEntity> textRoles = roles.stream()
                .filter(Objects::nonNull)
                .map(v -> {
                    TextRoleEntity textRoleEntity = new TextRoleEntity();
                    textRoleEntity.setProjectId(projectId);
                    textRoleEntity.setChapterId(chapterId);
                    textRoleEntity.setRole(v);
                    return textRoleEntity;
                }).toList();

        textRoleService.saveBatch(textRoles);
    }

    @Override
    public void updateProject(TextProjectEntity textProjectEntity) {
        TextProjectEntity project = textProjectService.getByProjectId(textProjectEntity.getProjectId());
        if (Objects.isNull(project)) {
            throw new BizException("项目不存在");
        }
        project.setProjectName(textProjectEntity.getProjectName());
        textProjectService.update(new LambdaUpdateWrapper<TextProjectEntity>()
                .eq(TextProjectEntity::getProjectId, textProjectEntity.getProjectId())
                .set(TextProjectEntity::getProjectName, textProjectEntity.getProjectName()));
    }

    @Override
    public void deleteProject(TextProjectEntity textProjectEntity) throws IOException {
        TextProjectEntity project = textProjectService.getByProjectId(textProjectEntity.getProjectId());
        if (Objects.isNull(project)) {
            log.error("text project is null");
        }

        chapterInfoService.deleteByProjectId(textProjectEntity.getProjectId());
        textChapterService.deleteByProjectId(textProjectEntity.getProjectId());
        textProjectService.deleteByProjectId(textProjectEntity.getProjectId());
        textRoleService.deleteByProjectId(textProjectEntity.getProjectId());
        textCommonRoleService.deleteByProjectId(textProjectEntity.getProjectId());
        textRoleInferenceService.deleteByProjectId(textProjectEntity.getProjectId());

        FileUtils.deleteDirectoryAll(Path.of(
                envConfig.getProjectDir(),
                "text",
                FileUtils.fileNameFormat(textProjectEntity.getProjectName())
        ));
    }

    @Override
    public List<String> tmpChapterSplit(String projectId, String chapterPattern, String dialoguePattern) {
        TextProjectEntity projectEntity = textProjectService.getAndContentByProjectId(projectId);
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
        TextProjectEntity projectEntity = textProjectService.getAndContentByProjectId(projectId);
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

            textChapterService.deleteByProjectId(projectId);
            textChapterService.saveBatch(textChapterEntities);
        }

        projectEntity.setChapterPattern(chapterPattern);
        textProjectService.updateById(projectEntity);
    }
}
