package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;

import java.io.IOException;
import java.util.List;

public interface BTextProjectService {

    List<TextProject> list();

    void create(String name, String content);

    List<String> tmpChapterSplit(String projectId, String chapterPattern, String dialoguePattern);

    void chapterSplit(String projectId, String chapterPattern, String dialoguePattern);

    void update(TextProjectEntity textProjectEntity);

    void delete(TextProjectEntity textProjectEntity) throws IOException;
}
