package space.wenliang.ai.aigcplatformserver.service.business;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.*;
import space.wenliang.ai.aigcplatformserver.common.Page;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.io.IOException;
import java.util.List;

public interface BTextChapterService {

    Page<TextChapterEntity> pageChapters(ProjectQuery projectQuery);

    List<TextChapterEntity> chapters4Sort(String projectId);

    void deleteChapter(TextChapterEntity textChapter) throws IOException;

    List<ChapterInfoEntity> tmpDialogueParse(TextChapterEntity textChapter);

    void chapterEdit(TextChapterEntity textChapter);

    void chapterAdd(ChapterAdd chapterAdd);

    void chapterSort(List<TextChapterEntity> sortChapters);

    List<TextRoleEntity> roles(String chapterId);

    void updateRole(TextRoleEntity textRoleEntity);

    void updateRoleModel(UpdateModelInfo updateModelInfo);

    void roleCombine(String projectId, String chapterId, String fromRoleName, String toRoleName);

    void textRoleChange(TextRoleChange textRoleChange);

    Boolean saveToCommonRole(TextRoleEntity textRoleEntity);

    List<TextCommonRoleEntity> commonRoles(String projectId);

    void createCommonRole(TextCommonRoleEntity textCommonRoleEntity);

    void updateCommonRole(UpdateModelInfo updateModelInfo);

    void deleteCommonRole(TextCommonRoleEntity textCommonRoleEntity);

    Object checkRoleInference(String projectId, String chapterId);

    void loadRoleInference(String projectId, String chapterId);

    void chapterExpose(ChapterExpose chapterExpose) throws Exception;

    Flux<String> roleInference(String projectId, String chapterId);
}
