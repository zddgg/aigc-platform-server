package space.wenliang.ai.aigcplatformserver.service.business.impl;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.ai.audio.creator.EdgeTtsCreator;
import space.wenliang.ai.aigcplatformserver.bean.EdgeTtsVoice;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsSettingEntity;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.AEdgeTtsConfigService;
import space.wenliang.ai.aigcplatformserver.service.application.AEdgeTtsSettingService;
import space.wenliang.ai.aigcplatformserver.service.application.ARefAudioService;
import space.wenliang.ai.aigcplatformserver.service.business.BEdgeTtsService;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BEdgeTtsServiceImpl implements BEdgeTtsService {

    private final PathConfig pathConfig;
    private final AudioCreator audioCreator;
    private final EdgeTtsCreator edgeTtsCreator;
    private final ARefAudioService aRefAudioService;
    private final AEdgeTtsConfigService aEdgeTtsConfigService;
    private final AEdgeTtsSettingService aEdgeTtsSettingService;

    @Override
    public List<EdgeTtsConfigEntity> configs() {
        List<String> showList = aEdgeTtsSettingService.list()
                .stream()
                .filter(c -> Objects.equals(c.getShowFlag(), Boolean.TRUE))
                .map(EdgeTtsSettingEntity::getEnName)
                .toList();
        return aEdgeTtsConfigService.list()
                .stream()
                .filter(v -> showList.contains(v.getLocale().substring(0, v.getLocale().indexOf("-"))))
                .toList();
    }

    @Override
    public List<EdgeTtsSettingEntity> settings() {
        List<EdgeTtsSettingEntity> settingEntities = aEdgeTtsSettingService.list();
        List<EdgeTtsConfigEntity> configEntities = aEdgeTtsConfigService.list();

        Set<String> enNameSet = settingEntities.stream().map(EdgeTtsSettingEntity::getEnName)
                .collect(Collectors.toSet());

        List<EdgeTtsSettingEntity> newList = configEntities.stream().map(c -> c.getLocale().substring(0, c.getLocale().indexOf("-")))
                .distinct()
                .filter(n -> !enNameSet.contains(n))
                .map(n -> {
                    EdgeTtsSettingEntity edgeTtsSettingEntity = new EdgeTtsSettingEntity();
                    edgeTtsSettingEntity.setEnName(n);
                    return edgeTtsSettingEntity;
                })
                .toList();

        settingEntities.addAll(newList);

        return settingEntities;
    }

    @Override
    public void updateSetting(EdgeTtsSettingEntity edgeTtsSettingEntity) {
        aEdgeTtsSettingService.saveOrUpdate(edgeTtsSettingEntity);
    }

    @Override
    public void syncConfigs() {
        List<EdgeTtsVoice> edgeTtsVoices = edgeTtsCreator.getEdgeTtsVoices();

        List<EdgeTtsConfigEntity> edgeTtsVoiceEntities = edgeTtsVoices.stream().map(edgeTtsVoice -> {
            EdgeTtsConfigEntity edgeTtsConfigEntity = new EdgeTtsConfigEntity();
            edgeTtsConfigEntity.setConfigId(edgeTtsVoice.getShortName());
            edgeTtsConfigEntity.setName(edgeTtsVoice.getName());
            edgeTtsConfigEntity.setShortName(edgeTtsVoice.getShortName());
            edgeTtsConfigEntity.setGender(edgeTtsVoice.getGender());
            edgeTtsConfigEntity.setLocale(edgeTtsVoice.getLocale());
            edgeTtsConfigEntity.setSuggestedCodec(edgeTtsVoice.getSuggestedCodec());
            edgeTtsConfigEntity.setFriendlyName(edgeTtsVoice.getFriendlyName());
            edgeTtsConfigEntity.setStatus(edgeTtsVoice.getStatus());
            edgeTtsConfigEntity.setVoiceTag(JSON.toJSONString(edgeTtsVoice.getVoiceTag()));
            return edgeTtsConfigEntity;
        }).toList();

        aEdgeTtsConfigService.remove(null);
        aEdgeTtsConfigService.saveBatch(edgeTtsVoiceEntities);

        List<EdgeTtsSettingEntity> edgeTtsConfigEntities = aEdgeTtsSettingService.list();

        Map<String, EdgeTtsSettingEntity> configEntityMap = edgeTtsConfigEntities.stream()
                .collect(Collectors.toMap(EdgeTtsSettingEntity::getEnName, Function.identity(), (a, _) -> a));

        Set<String> langSet = edgeTtsVoices.stream().map(edgeTtsVoice -> edgeTtsVoice.getLocale().substring(0, edgeTtsVoice.getLocale().indexOf('-')))
                .collect(Collectors.toSet());

        List<String> deleteEnNames = new ArrayList<>();
        edgeTtsConfigEntities.forEach(edgeTtsConfigEntity -> {
            if (!langSet.contains(edgeTtsConfigEntity.getEnName())) {
                deleteEnNames.add(edgeTtsConfigEntity.getEnName());
            }
        });

        List<EdgeTtsSettingEntity> addConfigs = new ArrayList<>();
        langSet.forEach(lang -> {
            if (!configEntityMap.containsKey(lang)) {
                EdgeTtsSettingEntity edgeTtsSettingEntity = new EdgeTtsSettingEntity();
                edgeTtsSettingEntity.setEnName(lang);
                addConfigs.add(edgeTtsSettingEntity);
            }
        });

        aEdgeTtsSettingService.deleteByEnNames(deleteEnNames);
        aEdgeTtsSettingService.saveBatch(addConfigs);
    }

    @Override
    public String playOrCreateAudio(String voice) {
        List<RefAudioEntity> refAudioEntities = aRefAudioService.getByGroupAndName("edge-tts", voice);

        if (!CollectionUtils.isEmpty(refAudioEntities)) {
            RefAudioEntity refAudio = refAudioEntities.getFirst();
            return pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, refAudio.getMoodName(), refAudio.getMoodAudioName());
        }

        List<EdgeTtsConfigEntity> edgeTtsVoiceEntities = configs();
        List<EdgeTtsSettingEntity> edgeTtsConfigEntities = settings();

        Optional<EdgeTtsConfigEntity> voiceOptional = edgeTtsVoiceEntities
                .stream()
                .filter(v -> StringUtils.equals(v.getShortName(), voice)).findFirst();
        if (voiceOptional.isEmpty()) {
            throw new BizException("edge-tts speaker [" + voice + "] not found");
        }


        Optional<EdgeTtsSettingEntity> configOptional = edgeTtsConfigEntities
                .stream()
                .filter(f -> StringUtils.equals(f.getEnName(),
                        voiceOptional.get().getLocale().substring(0, voiceOptional.get().getLocale().indexOf("-"))))
                .findFirst();
        if (configOptional.isEmpty() || StringUtils.isBlank(configOptional.get().getText())) {
            throw new BizException("edge-tts speak text not configured");
        }

        AudioContext audioContext = new AudioContext();

        audioContext.setType("edge-tts");

        String text = configOptional.get().getText();
        audioContext.setText(text);
        audioContext.setOutputDir(pathConfig.buildModelPath("ref-audio", "edge-tts", voice, "默认").toAbsolutePath().toString());
        audioContext.setOutputName(text);

        EdgeTtsConfigEntity edgeTtsConfig = new EdgeTtsConfigEntity();
        edgeTtsConfig.setShortName(voice);
        audioContext.setEdgeTtsConfig(edgeTtsConfig);

        audioCreator.createFile(audioContext);

        return pathConfig.buildModelUrl("ref-audio", "edge-tts", voice, "默认", FileUtils.fileNameFormat(text + ".wav"));
    }
}
