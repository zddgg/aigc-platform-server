package space.wenliang.ai.aigcplatformserver.service.business.impl;

import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.application.ATextRoleService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextRoleService;

import java.util.List;

@Service
public class BTextRoleServiceImpl implements BTextRoleService {

    private final ATextRoleService aTextRoleService;

    public BTextRoleServiceImpl(ATextRoleService aTextRoleService) {
        this.aTextRoleService = aTextRoleService;
    }

    @Override
    public List<TextRoleEntity> list(String projectId, String chapterId) {
        return aTextRoleService.list(projectId, chapterId);
    }
}
