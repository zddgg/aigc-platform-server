package space.wenliang.ai.aigcplatformserver.ai.audio;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.entity.*;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AudioCreator {

    private final Map<String, IAudioCreator> audioCreatorMap;
    private final AAudioServerConfigService aAudioServerConfigService;

    private final ARefAudioService aRefAudioService;
    private final AGptSovitsModelService aGptSovitsModelService;
    private final AGptSovitsConfigService aGptSovitsConfigService;
    private final AFishSpeechModelService aFishSpeechModelService;
    private final AFishSpeechConfigService aFishSpeechConfigService;
    private final AChatTtsConfigService aChatTtsConfigService;
    private final AEdgeTtsConfigService aEdgeTtsConfigService;

    public AudioCreator(Map<String, IAudioCreator> audioCreatorMap,
                        AAudioServerConfigService aAudioServerConfigService,
                        ARefAudioService aRefAudioService,
                        AGptSovitsModelService aGptSovitsModelService,
                        AGptSovitsConfigService aGptSovitsConfigService,
                        AFishSpeechModelService aFishSpeechModelService,
                        AFishSpeechConfigService aFishSpeechConfigService,
                        AChatTtsConfigService aChatTtsConfigService,
                        AEdgeTtsConfigService aEdgeTtsConfigService) {
        this.audioCreatorMap = audioCreatorMap;
        this.aAudioServerConfigService = aAudioServerConfigService;
        this.aRefAudioService = aRefAudioService;
        this.aGptSovitsModelService = aGptSovitsModelService;
        this.aGptSovitsConfigService = aGptSovitsConfigService;
        this.aFishSpeechModelService = aFishSpeechModelService;
        this.aFishSpeechConfigService = aFishSpeechConfigService;
        this.aChatTtsConfigService = aChatTtsConfigService;
        this.aEdgeTtsConfigService = aEdgeTtsConfigService;
    }

    public ResponseEntity<byte[]> createAudio(AudioContext context) {
        prepareAudioServer(context);

        if (audioCreatorMap.containsKey(context.getType())) {
            return audioCreatorMap.get(context.getType()).createAudio(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }

    public void createFile(AudioContext context) {
        prepareAudioServer(context);

        if (audioCreatorMap.containsKey(context.getType())) {
            audioCreatorMap.get(context.getType()).createFile(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }

    private void prepareAudioServer(AudioContext context) {
        List<AudioServerConfigEntity> audioServerConfigs = aAudioServerConfigService.list();
        Map<String, AudioServerConfigEntity> audioServerMap = audioServerConfigs.stream()
                .collect(Collectors.toMap(AudioServerConfigEntity::getName, Function.identity()));

        context.setAudioServerConfig(audioServerMap.get(context.getType()));


        Map<String, RefAudioEntity> refAudioEntityMap = aRefAudioService.list()
                .stream()
                .collect(Collectors.toMap(RefAudioEntity::getRefAudioId, Function.identity()));
        Map<String, GptSovitsModelEntity> gptSovitsModelEntityMap = aGptSovitsModelService.list()
                .stream()
                .collect(Collectors.toMap(GptSovitsModelEntity::getModelId, Function.identity()));
        Map<String, GptSovitsConfigEntity> gptSovitsConfigEntityMap = aGptSovitsConfigService.list()
                .stream()
                .collect(Collectors.toMap(GptSovitsConfigEntity::getConfigId, Function.identity()));
        Map<String, FishSpeechModelEntity> fishSpeechModelEntityMap = aFishSpeechModelService.list()
                .stream()
                .collect(Collectors.toMap(FishSpeechModelEntity::getModelId, Function.identity()));
        Map<String, FishSpeechConfigEntity> fishSpeechConfigEntityMap = aFishSpeechConfigService.list()
                .stream()
                .collect(Collectors.toMap(FishSpeechConfigEntity::getConfigId, Function.identity()));
        Map<String, ChatTtsConfigEntity> chatTtsConfigEntityMap = aChatTtsConfigService.list()
                .stream()
                .collect(Collectors.toMap(ChatTtsConfigEntity::getConfigId, Function.identity()));
        Map<String, EdgeTtsConfigEntity> edgeTtsConfigEntityMap = aEdgeTtsConfigService.list()
                .stream()
                .collect(Collectors.toMap(EdgeTtsConfigEntity::getConfigId, Function.identity()));

        if (StringUtils.equals(context.getType(), ModelTypeEnum.gpt_sovits.getName())) {
            context.setGptSovitsModel(gptSovitsModelEntityMap.get(context.getModelId()));
            if (Objects.isNull(context.getGptSovitsConfig())) {
                context.setGptSovitsConfig(gptSovitsConfigEntityMap.get(context.getConfigId()));
            }
            context.setRefAudio(refAudioEntityMap.get(context.getRefAudioId()));
        }
        if (StringUtils.equals(context.getType(), ModelTypeEnum.fish_speech.getName())) {
            context.setFishSpeechModel(fishSpeechModelEntityMap.get(context.getModelId()));
            if (Objects.isNull(context.getFishSpeechConfig())) {
                context.setFishSpeechConfig(fishSpeechConfigEntityMap.get(context.getConfigId()));
            }
            context.setRefAudio(refAudioEntityMap.get(context.getRefAudioId()));
        }
        if (StringUtils.equals(context.getType(), ModelTypeEnum.chat_tts.getName())) {
            if (Objects.isNull(context.getChatTtsConfig())) {
                context.setChatTtsConfig(chatTtsConfigEntityMap.get(context.getConfigId()));
            }
        }
        if (StringUtils.equals(context.getType(), ModelTypeEnum.edge_tts.getName())) {
            if (Objects.isNull(context.getEdgeTtsConfig())) {
                context.setEdgeTtsConfig(edgeTtsConfigEntityMap.get(context.getConfigId()));
            }
        }

    }
}
