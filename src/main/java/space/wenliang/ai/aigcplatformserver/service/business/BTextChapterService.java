package space.wenliang.ai.aigcplatformserver.service.business;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.ChapterAdd;
import space.wenliang.ai.aigcplatformserver.bean.ChapterExpose;
import space.wenliang.ai.aigcplatformserver.bean.ProjectQuery;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
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

    List<ChapterInfoEntity> tmpDialogueParse(TextChapterEntity textChapter);

    void chapterEdit(TextChapterEntity textChapter);

    void chapterAdd(ChapterAdd chapterAdd);

    void chapterSort(List<TextChapterEntity> sortChapters);

    Flux<String> roleInference(String projectId, String chapterId);

    Boolean checkRoleInference(String projectId, String chapterId);

    void loadRoleInference(String projectId, String chapterId);

    TextChapterEntity getTextChapterAndContent(String projectId, String chapterId);

    List<TextRoleEntity> roles(String projectId, String chapterId);

    List<TextCommonRoleEntity> commonRoles(String projectId);

    void createCommonRole(TextCommonRoleEntity textCommonRoleEntity);

    void updateCommonRole(TextCommonRoleEntity textCommonRoleEntity);

    void deleteCommonRole(TextCommonRoleEntity textCommonRoleEntity);

    void updateRoleName(TextRoleEntity textRoleEntity);

    void updateRole(TextRoleEntity textRoleEntity);

    void textRoleChange(TextRoleChange textRoleChange);

    void roleCombine(String projectId, String chapterId, String fromRoleName, String toRoleName);

    void chapterExpose(ChapterExpose chapterExpose);

    void deleteChapter(TextChapterEntity textChapter) throws IOException;

    Boolean saveToCommonRole(TextRoleEntity textRoleEntity);
}
