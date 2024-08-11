package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.bean.FormatTextProject;
import space.wenliang.ai.aigcplatformserver.bean.TextProject;
import space.wenliang.ai.aigcplatformserver.entity.TextProjectEntity;

import java.io.IOException;
import java.util.List;

public interface BTextProjectService {

    List<TextProject> projectList();

    TextProjectEntity getByProjectId(String projectId);

    void createProject(String project, String projectType, String content);

    void createFormatTextProject(FormatTextProject project);

    void updateProject(TextProjectEntity textProjectEntity);

    void deleteProject(TextProjectEntity textProjectEntity) throws IOException;

    List<String> tmpChapterSplit(String projectId, String chapterPattern, String dialoguePattern);

    void chapterSplit(String projectId, String chapterPattern, String dialoguePattern);
}
