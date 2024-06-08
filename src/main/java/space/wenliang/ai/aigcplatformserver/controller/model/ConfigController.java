package space.wenliang.ai.aigcplatformserver.controller.model;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.model.*;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;
import space.wenliang.ai.aigcplatformserver.service.ModelService;
import space.wenliang.ai.aigcplatformserver.service.PathService;
import space.wenliang.ai.aigcplatformserver.utils.FileUtils;
import space.wenliang.ai.aigcplatformserver.utils.IdUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("config")
public class ConfigController {

    private final PathService pathService;
    private final ConfigService configService;
    private final ModelService modelService;

    public ConfigController(PathService pathService, ConfigService configService, ModelService modelService) {
        this.pathService = pathService;
        this.configService = configService;
        this.modelService = modelService;
    }

    @PostMapping("chat/queryChatConfig")
    public Result<Object> queryChatConfig() throws Exception {
        ChatConfig config = configService.getChatConfig();
        return Result.success(config);
    }

    @PostMapping("chat/updateChatConfig")
    public Result<Object> updateChatConfig(@RequestBody ChatModelParam chatModelParam) throws Exception {

        if (Objects.isNull(chatModelParam.getId())) {
            chatModelParam.setId(IdUtil.uuid());
        }

        ChatConfig chatConfig = configService.getChatConfig();
        for (ChatModelParam modelParam : chatConfig.getServices()) {
            if (Objects.nonNull(modelParam.getId())) {
                if (StringUtils.equals(modelParam.getName(), chatModelParam.getName())
                        && !Objects.equals(modelParam.getId(), chatModelParam.getId())) {
                    throw new BizException("配置[" + chatModelParam.getName() + "]已存在");
                }
            }
        }

        List<ChatModelParam> newList = chatConfig.getServices().stream()
                .filter((value -> !Objects.equals(value.getId(), chatModelParam.getId())))
                .toList();
        List<ChatModelParam> saveList = new ArrayList<>(newList);
        saveList.add(chatModelParam);
        chatConfig.setServices(saveList);

        configService.saveChatConfig(chatConfig);
        return Result.success();
    }

    @PostMapping("chat/deleteChatConfig")
    public Result<Object> deleteChatConfig(@RequestBody ChatModelParam chatModelParam) throws Exception {

        ChatConfig chatConfig = configService.getChatConfig();
        List<ChatModelParam> saveList = chatConfig.getServices().stream()
                .filter(value -> !Objects.equals(value.getId(), chatModelParam.getId()))
                .toList();

        chatConfig.setServices(saveList);
        configService.saveChatConfig(chatConfig);

        return Result.success();
    }

    @PostMapping("chat/activeChatConfig")
    public Result<Object> activeChatConfig(@RequestBody ChatModelParam chatModelParam) throws Exception {

        ChatConfig chatConfig = configService.getChatConfig();
        List<ChatModelParam> saveList = chatConfig.getServices().stream()
                .peek(value -> {
                    value.setActive(Objects.equals(value.getId(), chatModelParam.getId()));
                })
                .toList();

        chatConfig.setServices(saveList);
        configService.saveChatConfig(chatConfig);

        return Result.success();
    }

    @PostMapping("audioServer/queryAudioServerConfig")
    public Result<Object> queryAudioServerConfig() throws Exception {
        List<AudioServerConfig> configs = configService.getAudioServerConfigs();
        return Result.success(configs);
    }

    @PostMapping("audioServer/updateAudioServerConfig")
    public Result<Object> updateAudioServerConfig(@RequestBody AudioServerConfig config) throws Exception {
        List<AudioServerConfig> configs = configService.getAudioServerConfigs();
        for (AudioServerConfig audioServerConfig : configs) {
            if (StringUtils.equals(audioServerConfig.getName(), config.getName())) {
                audioServerConfig.setServerUrl(config.getServerUrl());
                audioServerConfig.setApiVersion(config.getApiVersion());
            }
        }

        configService.saveAudioServerConfigs(configs);
        return Result.success();
    }

    @PostMapping("edge-tts/queryEdgeTtsConfig")
    public Result<Object> queryEdgeTtsConfig() throws Exception {
        EdgeTtsConfig edgeTtsConfig = configService.getEdgeTtsConfig();
        List<EdgeTtsConfig.LangText> langTexts = edgeTtsConfig.getLangTexts();
        List<String> filters = langTexts.stream()
                .filter(l -> Objects.equals(l.getShow(), Boolean.TRUE))
                .map(EdgeTtsConfig.LangText::getEnName)
                .toList();


        List<EdgeTtsVoice> voices = edgeTtsConfig.getVoices()
                .stream()
                .filter(v -> filters.contains(v.getLocale().substring(0, v.getLocale().indexOf('-'))))
                .toList();

        List<EdgeTtsConfig.LangText> addLangTexts = voices.stream()
                .map(EdgeTtsVoice::getLocale)
                .map(s -> s.substring(0, s.indexOf('-')))
                .filter(s -> !langTexts.stream().map(EdgeTtsConfig.LangText::getEnName).distinct().toList().contains(s))
                .distinct()
                .map(s -> {
                    EdgeTtsConfig.LangText langText = new EdgeTtsConfig.LangText();
                    langText.setEnName(s);
                    return langText;
                }).toList();
        langTexts.addAll(addLangTexts);
        langTexts.sort(Comparator.comparing(v -> !filters.contains(v.getEnName())));

        Map<String, Tuple3<String, String, String>> etAudiomap = modelService.getAudios().stream()
                .filter(refAudio -> StringUtils.equals(refAudio.getGroup(), "edge-tts"))
                .collect(Collectors.toMap(RefAudio::getName,
                        v -> Tuple.of(v.getMoods().getFirst().getMoodAudios().getFirst().getUrl(),
                                v.getMoods().getFirst().getMoodAudios().getFirst().getText(),
                                v.getAvatar())));

        voices = voices.stream()
                .peek(voice -> {
                    if (etAudiomap.containsKey(voice.getShortName())) {
                        voice.setUrl(etAudiomap.get(voice.getShortName())._1);
                        voice.setText(etAudiomap.get(voice.getShortName())._2);
                        voice.setAvatar(etAudiomap.get(voice.getShortName())._3);
                    }
                })
                .sorted(Comparator.comparing(v -> !filters.contains(v.getLocale().substring(0, v.getLocale().indexOf('-')))))
                .toList();

        edgeTtsConfig.setVoices(voices);
        edgeTtsConfig.setLangTexts(langTexts);

        return Result.success(edgeTtsConfig);
    }

    @PostMapping("edge-tts/updateEdgeTtsConfig")
    public Result<Object> updateEdgeTtsConfig(@RequestBody EdgeTtsConfig.LangText langText) throws Exception {
        EdgeTtsConfig edgeTtsConfig = configService.getEdgeTtsConfig();
        List<EdgeTtsVoice> voices = edgeTtsConfig.getVoices();
        List<EdgeTtsConfig.LangText> langTexts = edgeTtsConfig.getLangTexts();

        List<EdgeTtsConfig.LangText> addLangTexts = voices.stream()
                .map(EdgeTtsVoice::getLocale)
                .map(s -> s.substring(0, s.indexOf('-')))
                .filter(s -> !langTexts.stream().map(EdgeTtsConfig.LangText::getEnName).distinct().toList().contains(s))
                .distinct()
                .map(EdgeTtsConfig.LangText::new).toList();
        langTexts.addAll(addLangTexts);

        for (EdgeTtsConfig.LangText text : langTexts) {
            if (StringUtils.equals(text.getEnName(), langText.getEnName())) {
                text.setZhName(langText.getZhName());
                text.setText(langText.getText());
                text.setShow(langText.getShow());
            }
        }

        configService.saveEdgeTtsConfig(edgeTtsConfig);
        return Result.success();
    }

    @PostMapping("chat-tts/queryChatTtsConfig")
    public Result<Object> queryChatTtsConfig() throws Exception {
        List<ChatTtsConfig> chatTtsConfigs = configService.getChatTtsConfig();

        Map<String, Tuple2<String, String>> audiomap = modelService.getAudios().stream()
                .filter(refAudio -> StringUtils.equals(refAudio.getGroup(), "chat-tts"))
                .collect(Collectors.toMap(RefAudio::getName,
                        v -> Tuple.of(v.getMoods().getFirst().getMoodAudios().getFirst().getUrl(),
                                v.getMoods().getFirst().getMoodAudios().getFirst().getText())));

        chatTtsConfigs = chatTtsConfigs.stream()
                .peek(config -> {
                    if (audiomap.containsKey(config.getConfigName())) {
                        config.setUrl(audiomap.get(config.getConfigName())._1);
                        config.setText(audiomap.get(config.getConfigName())._2);
                    }
                })
                .toList();

        return Result.success(chatTtsConfigs);
    }

    @PostMapping("chat-tts/createChatTtsConfig")
    public Result<Object> createChatTtsConfig(@RequestParam("configName") String configName,
                                              @RequestParam("saveAudio") Boolean saveAudio,
                                              @RequestParam("temperature") Float temperature,
                                              @RequestParam("top_P") Float top_P,
                                              @RequestParam("top_K") Integer top_K,
                                              @RequestParam("audio_seed_input") Integer audio_seed_input,
                                              @RequestParam("text_seed_input") Integer text_seed_input,
                                              @RequestParam("refine_text_flag") Boolean refine_text_flag,
                                              @RequestParam("params_refine_text") String params_refine_text,
                                              @RequestParam(value = "file", required = false) MultipartFile file,
                                              @RequestParam(value = "outputText", required = false) String outputText) throws Exception {
        List<ChatTtsConfig> chatTtsConfigs = configService.getChatTtsConfig();
        if (CollectionUtils.isEmpty(chatTtsConfigs)) {
            chatTtsConfigs = new ArrayList<>();
        }
        chatTtsConfigs.stream().filter(v -> StringUtils.equals(v.getConfigName(), configName))
                .findAny().ifPresent(r -> {
                    throw new BizException("配置名称[" + configName + "]已存在");
                });

        ChatTtsConfig chatTtsConfig = new ChatTtsConfig();
        chatTtsConfig.setConfigName(configName);
        chatTtsConfig.setTemperature(temperature);
        chatTtsConfig.setTop_P(top_P);
        chatTtsConfig.setTop_K(top_K);
        chatTtsConfig.setAudio_seed_input(audio_seed_input);
        chatTtsConfig.setText_seed_input(text_seed_input);
        chatTtsConfig.setRefine_text_flag(refine_text_flag);
        chatTtsConfig.setParams_refine_text(params_refine_text);

        chatTtsConfigs.add(chatTtsConfig);
        configService.saveChatTtsConfig(chatTtsConfigs);

        if (Objects.equals(saveAudio, Boolean.TRUE) && file != null && StringUtils.isNotBlank(outputText)) {
            String fileName = FileUtils.fileNameFormat(outputText);
            Path voiceAudioDir = pathService.buildModelPath("ref-audio", "chat-tts", configName, "默认", fileName + ".wav");
            if (Files.notExists(voiceAudioDir.getParent())) {
                Files.createDirectories(voiceAudioDir.getParent());
            }
            Files.write(voiceAudioDir, file.getBytes());
        }

        return Result.success();
    }

    @PostMapping("chat-tts/deleteChatTtsConfig")
    public Result<Object> deleteChatTtsConfig(@RequestBody ChatTtsConfig config) throws Exception {
        List<ChatTtsConfig> chatTtsConfigs = configService.getChatTtsConfig();
        chatTtsConfigs = chatTtsConfigs.stream()
                .filter(c -> !StringUtils.equals(c.getConfigName(), config.getConfigName()))
                .toList();
        configService.saveChatTtsConfig(chatTtsConfigs);
        return Result.success();
    }
}
