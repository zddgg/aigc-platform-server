package space.wenliang.ai.aigcplatformserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.TmPromptTemplateEntity;
import space.wenliang.ai.aigcplatformserver.service.TmPromptTemplateService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("tmPromptTemplate")
@RequiredArgsConstructor
public class TmPromptTemplateController {

    private final TmPromptTemplateService tmPromptTemplateService;

    @PostMapping("list")
    public Result<Object> list(@SingleValueParam("templateGroup") String templateGroup,
                               @SingleValueParam("isDefault") Boolean isDefault) {
        List<TmPromptTemplateEntity> list = tmPromptTemplateService.list(
                        new LambdaQueryWrapper<TmPromptTemplateEntity>()
                                .eq(StringUtils.isNotBlank(templateGroup), TmPromptTemplateEntity::getTemplateGroup, templateGroup)
                                .eq(Objects.nonNull(isDefault), TmPromptTemplateEntity::getIsDefault, isDefault))
                .stream()
                .sorted(Comparator.comparing(TmPromptTemplateEntity::getTemplateGroup)
                        .thenComparing(v -> !Objects.equals(v.getIsPreset(), Boolean.TRUE)))
                .toList();
        return Result.success(list);
    }

    @PostMapping("edit")
    public Result<Object> edit(@RequestBody TmPromptTemplateEntity promptTemplateEntity) {
        List<TmPromptTemplateEntity> list = tmPromptTemplateService.list(
                new LambdaQueryWrapper<TmPromptTemplateEntity>()
                        .eq(TmPromptTemplateEntity::getTemplateGroup, promptTemplateEntity.getTemplateGroup())
                        .eq(TmPromptTemplateEntity::getIsDefault, true));

        if (Objects.isNull(promptTemplateEntity.getId())) {
            promptTemplateEntity.setIsDefault(CollectionUtils.isEmpty(list));
        }
        promptTemplateEntity.setIsPreset(false);
        tmPromptTemplateService.saveOrUpdate(promptTemplateEntity);
        return Result.success();
    }

    @PostMapping("delete")
    public Result<Object> delete(@RequestBody TmPromptTemplateEntity promptTemplateEntity) {
        if (tmPromptTemplateService.removeById(promptTemplateEntity) && Objects.equals(promptTemplateEntity.getIsDefault(), Boolean.TRUE)) {
            tmPromptTemplateService.update(
                    new LambdaUpdateWrapper<TmPromptTemplateEntity>()
                            .set(TmPromptTemplateEntity::getIsDefault, true)
                            .eq(TmPromptTemplateEntity::getTemplateGroup, promptTemplateEntity.getTemplateGroup())
                            .eq(TmPromptTemplateEntity::getIsPreset, true));
        }
        return Result.success();
    }

    @PostMapping("setDefault")
    public Result<Object> setDefault(@RequestBody TmPromptTemplateEntity promptTemplateEntity) {
        List<TmPromptTemplateEntity> list = tmPromptTemplateService.list(
                new LambdaQueryWrapper<TmPromptTemplateEntity>()
                        .eq(TmPromptTemplateEntity::getTemplateGroup, promptTemplateEntity.getTemplateGroup()));
        List<Integer> customPromptTemplateIds = list.stream().map(TmPromptTemplateEntity::getId)
                .filter(id -> !Objects.equals(id, promptTemplateEntity.getId()))
                .toList();
        if (!CollectionUtils.isEmpty(customPromptTemplateIds)) {
            tmPromptTemplateService.update(
                    new LambdaUpdateWrapper<TmPromptTemplateEntity>()
                            .set(TmPromptTemplateEntity::getIsDefault, false)
                            .in(TmPromptTemplateEntity::getId, customPromptTemplateIds));
        }
        tmPromptTemplateService.update(
                new LambdaUpdateWrapper<TmPromptTemplateEntity>()
                        .set(TmPromptTemplateEntity::getIsDefault, true)
                        .eq(TmPromptTemplateEntity::getId, promptTemplateEntity.getId()));
        return Result.success();
    }
}
