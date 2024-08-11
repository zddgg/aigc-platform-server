package space.wenliang.ai.aigcplatformserver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.ai.audio.creator.EdgeTtsCreator;
import space.wenliang.ai.aigcplatformserver.bean.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.AmModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.mapper.AmModelConfigMapper;
import space.wenliang.ai.aigcplatformserver.service.AmModelConfigService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmModelConfigServiceImpl extends ServiceImpl<AmModelConfigMapper, AmModelConfigEntity>
        implements AmModelConfigService {

    private final EnvConfig envConfig;
    private final EdgeTtsCreator edgeTtsCreator;

    @Override
    public AmModelConfigEntity getByMcId(String mcId) {
        return this.getOne(new LambdaQueryWrapper<AmModelConfigEntity>().eq(AmModelConfigEntity::getMcId, mcId));
    }

    @Override
    public List<AmModelConfigEntity> getByModelType(String modelType, Integer showMode) {
        return this.list(new LambdaQueryWrapper<AmModelConfigEntity>()
                .eq(AmModelConfigEntity::getAmType, modelType)
                .eq(Objects.nonNull(showMode) && !Objects.equals(showMode, -1), AmModelConfigEntity::getShowFlag, Objects.equals(showMode, 1)));
    }

    @Override
    public void updateConfig(AmModelConfigEntity modelConfig) {
        this.updateById(modelConfig);
    }

    @Override
    public void syncEdgeTtsConfig() throws IOException {
        List<EdgeTtsVoice> edgeTtsVoices = edgeTtsCreator.getVoices();

        Map<String, EdgeTtsVoice> voiceMap = edgeTtsVoices.stream()
                .collect(Collectors.toMap(EdgeTtsVoice::getShortName, Function.identity(), (_, b) -> b));

        List<AmModelConfigEntity> modelConfigs = this.list(new LambdaQueryWrapper<AmModelConfigEntity>()
                .eq(AmModelConfigEntity::getAmType, ModelTypeEnum.edge_tts.getName()));
        Map<String, AmModelConfigEntity> configMap = modelConfigs.stream()
                .collect(Collectors.toMap(AmModelConfigEntity::getMcName, Function.identity(), (_, b) -> b));


        List<AmModelConfigEntity> updatePromptAudios = new ArrayList<>();
        List<Integer> removeIds = new ArrayList<>();

        for (AmModelConfigEntity modelConfig : modelConfigs) {
            if (voiceMap.containsKey(modelConfig.getMcName())) {
                modelConfig.setMcParamsJson(JSON.toJSONString(voiceMap.get(modelConfig.getMcName())));

                updatePromptAudios.add(modelConfig);
            } else {
                removeIds.add(modelConfig.getId());
            }
        }

        List<AmModelConfigEntity> newPromptAudios = edgeTtsVoices.stream()
                .filter(modelFile -> !configMap.containsKey(modelFile.getShortName()))
                .map(v -> {
                    AmModelConfigEntity modelConfig = new AmModelConfigEntity();
                    modelConfig.setMcId(IdUtils.uuid());
                    modelConfig.setMcName(v.getShortName());
                    modelConfig.setAmType(ModelTypeEnum.edge_tts.getName());
                    modelConfig.setMcParamsJson(JSON.toJSONString(v));
                    return modelConfig;
                })
                .toList();

        if (!CollectionUtils.isEmpty(updatePromptAudios)) {
            this.updateBatchById(updatePromptAudios);
        }

        if (!CollectionUtils.isEmpty(removeIds)) {
            this.removeByIds(removeIds);
        }

        if (!CollectionUtils.isEmpty(newPromptAudios)) {
            this.saveBatch(newPromptAudios);
        }
    }

    @Override
    public void updateEdgeTtsShowFlag(Map<String, Boolean> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        List<AmModelConfigEntity> updateList = this.list(new LambdaQueryWrapper<AmModelConfigEntity>()
                        .eq(AmModelConfigEntity::getAmType, ModelTypeEnum.edge_tts.getName()))
                .stream()
                .map(item -> {
                    String locale = JSON.parseObject(item.getMcParamsJson()).getString("locale");
                    if (StringUtils.isNotBlank(locale)) {
                        String lang = locale.substring(0, locale.indexOf("-"));
                        if (StringUtils.isNotBlank(lang) && data.containsKey(lang)) {
                            AmModelConfigEntity modelConfig = new AmModelConfigEntity();
                            modelConfig.setId(item.getId());
                            modelConfig.setShowFlag(data.get(lang));
                            return modelConfig;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull).toList();
        if (!CollectionUtils.isEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
    }
}




