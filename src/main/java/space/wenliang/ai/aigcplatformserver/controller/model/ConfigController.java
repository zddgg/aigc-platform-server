package space.wenliang.ai.aigcplatformserver.controller.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.*;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;
import space.wenliang.ai.aigcplatformserver.utils.IdUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
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

    @PostMapping("edge-tts/config")
    public Result<Object> queryEdgeTtsConfig() throws Exception {
        EdgeTtsConfig edgeTtsConfig = configService.getEdgeTtsConfig();
        List<EdgeTtsVoice> voices = edgeTtsConfig.getVoices()
                .stream().filter(v -> List.of("zh", "en", "ja", "ko").contains(v.getLocale().substring(0, v.getLocale().indexOf('-'))))
                .toList();
        List<EdgeTtsConfig.LangText> langTexts = edgeTtsConfig.getLangTexts();

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
        langTexts.sort(Comparator.comparing(v -> !List.of("zh", "en", "ja", "ko").contains(v.getEnName())));
        voices = voices.stream()
                .sorted(Comparator.comparing(v -> !List.of("zh", "en", "ja", "ko").contains(v.getLocale().substring(0, v.getLocale().indexOf('-')))))
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
            }
        }

        configService.saveEdgeTtsConfig(edgeTtsConfig);
        return Result.success();
    }
}
