package space.wenliang.ai.aigcplatformserver.service.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.TextRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.application.ATextRoleService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextRoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BTextRoleServiceImpl implements BTextRoleService {

    private final ATextRoleService aTextRoleService;

    @Override
    public List<TextRoleEntity> list(String projectId, String chapterId) {
        return aTextRoleService.list(projectId, chapterId);
    }
}
