package space.wenliang.ai.aigcplatformserver.service;

import com.alibaba.fastjson2.JSON;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.text.Chapter;
import space.wenliang.ai.aigcplatformserver.bean.text.Role;
import space.wenliang.ai.aigcplatformserver.bean.text.RoleModelChange;
import space.wenliang.ai.aigcplatformserver.exception.BizException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final PathService pathService;

    public ProjectService(PathService pathService) {
        this.pathService = pathService;
    }

    @SneakyThrows
    public List<Role> getCommonRoles(String project) {
        Path commonRolesPath = pathService.getCommonRolesPath(project);
        if (Files.exists(commonRolesPath)) {
            return JSON.parseArray(Optional.ofNullable(Files.readString(commonRolesPath)).orElse("[]"), Role.class);
        }
        return new ArrayList<>();
    }

    @SneakyThrows
    public void saveCommonRoles(String project, List<Role> commonRoles) {
        Path commonRolesPath = pathService.getCommonRolesPath(project);
        Files.write(commonRolesPath, JSON.toJSONBytes(commonRoles));
    }

    public void createCommonRole(RoleModelChange roleModelChange) {
        Chapter chapter = roleModelChange.getChapter();
        Role role = roleModelChange.getRole();
        List<Role> commonRoles = getCommonRoles(chapter.getProject());
        commonRoles.stream().filter(r -> StringUtils.equals(r.getRole(), role.getRole()))
                .findFirst().ifPresent(r -> {
                    throw new BizException(STR."已存在预置角色[\{role.getRole()}]");
                });

        commonRoles.add(role);

        saveCommonRoles(chapter.getProject(), commonRoles);
    }

    public void deleteCommonRole(RoleModelChange roleModelChange) {
        Chapter chapter = roleModelChange.getChapter();
        List<Role> commonRoles = this.getCommonRoles(chapter.getProject());
        List<Role> saveList = commonRoles.stream()
                .filter(r -> !StringUtils.equals(r.getRole(), roleModelChange.getRole().getRole()))
                .toList();
        saveCommonRoles(chapter.getProject(), saveList);
    }
}
