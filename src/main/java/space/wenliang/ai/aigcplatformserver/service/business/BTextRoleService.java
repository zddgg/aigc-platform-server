package space.wenliang.ai.aigcplatformserver.service.business;

import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;

import java.util.List;

public interface BTextRoleService {

    List<TextRoleEntity> list(String projectId, String chapterId);
}
