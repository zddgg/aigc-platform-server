package space.wenliang.ai.aigcplatformserver.service.business;

import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.ChapterExpose;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.util.List;

public interface BTextChapterService {

    List<TextChapterEntity> chapters(String projectId);

    List<ChapterInfoEntity> tmpDialogueParse(String projectId, String chapterId, String dialoguePattern, String textContent);

    void dialogueParse(String projectId, String chapterId, String dialoguePattern, String textContent);

    Flux<String> roleInference(String projectId, String chapterId);

    Boolean checkRoleInference(String projectId, String chapterId);

    void loadRoleInference(String projectId, String chapterId);

    String getContent(String projectId, String chapterId);

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
}
