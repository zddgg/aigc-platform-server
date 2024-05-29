package space.wenliang.ai.aigcplatformserver.service;

import cn.hutool.core.io.file.FileNameUtil;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.model.*;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.utils.ForEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private final PathConfig pathConfig;

    public ModelService(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    public List<GsvModel> getModels(String type) throws IOException {
        List<GsvModel> gsvModels = buildModels(type);
        gsvModels.sort(Comparator.comparing(gsvModel -> !StringUtils.equals(gsvModel.getGroup(), "默认")));
        return gsvModels;
    }

    public List<RefAudio> getAudios() throws IOException {
        List<RefAudio> refAudios = buildAudios();

        Path audioConfigPath = Path.of(pathConfig.getFsDir(), "config", "ref-audio-config.json");
        if (Files.exists(audioConfigPath)) {
            List<RefAudio> refAudioConfigs = JSON.parseArray(Files.readString(audioConfigPath), RefAudio.class);
            if (!CollectionUtils.isEmpty(refAudioConfigs)) {
                refAudios = mergeAudioConfig(refAudios, refAudioConfigs);
            }
        }

        Files.write(audioConfigPath, JSON.toJSONString(refAudios).getBytes());

        return refAudios;
    }

    public List<GsvModel> buildModels(String type) throws IOException {
        Path gsvModelPath = Path.of(pathConfig.getFsDir(), "model", type);
        List<Group> gsvModelGroups = new ArrayList<>();
        List<GsvModel> gsvModels = new ArrayList<>();
        if (Files.exists(gsvModelPath)) {
            ForEach.forEach(Files.list(gsvModelPath), (groupIndex, group) -> {
                gsvModelGroups.add(new Group(groupIndex, group.getFileName().toString()));
                if (Files.isDirectory(group)) {
                    try {
                        ForEach.forEach(Files.list(group), (modelIndex, model) -> {
                            GsvModel gsvModel = new GsvModel();
                            gsvModel.setId(modelIndex);
                            gsvModel.setName(model.getFileName().toString());
                            gsvModel.setGroup(group.getFileName().toString());
                            if (Files.isDirectory(model)) {
                                try {
                                    ForEach.forEach(Files.list(model), (_, modelName) -> {
                                        if (modelName.getFileName().toString().endsWith("ckpt")) {
                                            gsvModel.setGptWeights(modelName.toFile().getName());
                                        }
                                        if (modelName.getFileName().toString().endsWith("pth")) {
                                            gsvModel.setSovitsWeights(modelName.toFile().getName());
                                        }

                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            gsvModels.add(gsvModel);
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        return gsvModels;
    }

    public List<RefAudio> buildAudios() throws IOException {
        String[] dirs = {"model", "ref-audio"};
        Path audioPath = Path.of(pathConfig.getFsDir(), dirs);
        List<RefAudio> refAudios = new ArrayList<>();
        if (Files.exists(audioPath)) {
            ForEach.forEach(Files.list(audioPath), (groupIndex, group) -> {
                if (Files.isDirectory(group)) {
                    try {
                        ForEach.forEach(Files.list(group), (modelIndex, timbre) -> {
                            RefAudio refAudio = new RefAudio();
                            refAudio.setId(modelIndex);
                            refAudio.setName(timbre.getFileName().toString());
                            refAudio.setGroup(group.getFileName().toString());
                            List<RefAudio.Mood> moods = new ArrayList<>();
                            if (Files.isDirectory(timbre)) {
                                try {
                                    ForEach.forEach(Files.list(timbre), (moodIndex, timbreMood) -> {
                                        if (Files.isDirectory(timbreMood)) {
                                            RefAudio.Mood mood = new RefAudio.Mood();
                                            mood.setId(moodIndex);
                                            mood.setName(timbreMood.getFileName().toString());
                                            List<RefAudio.MoodAudio> moodAudios = new ArrayList<>();
                                            try {
                                                ForEach.forEach(Files.list(timbreMood), (audioIndex, moodAudio) -> {
                                                    if (moodAudio.getFileName().toString().startsWith("avatar")) {
                                                        List<String> avatarDir = new ArrayList<>(List.of(dirs));
                                                        avatarDir.add(group.getFileName().toString());
                                                        avatarDir.add(timbre.getFileName().toString());
                                                        avatarDir.add(timbreMood.getFileName().toString());
                                                        avatarDir.add(moodAudio.getFileName().toString());
                                                        mood.setAvatar(pathConfig.buildFsUrl(avatarDir.toArray(new String[0])));
                                                    } else {
                                                        RefAudio.MoodAudio newMoodAudio = new RefAudio.MoodAudio();
                                                        newMoodAudio.setId(audioIndex);
                                                        newMoodAudio.setName(moodAudio.getFileName().toString());
                                                        newMoodAudio.setText(FileNameUtil.getPrefix(moodAudio.getFileName().toString()));
                                                        newMoodAudio.setUrl(pathConfig.buildFsUrl(
                                                                dirs,
                                                                group.getFileName().toString(),
                                                                timbre.getFileName().toString(),
                                                                timbreMood.getFileName().toString(),
                                                                moodAudio.getFileName().toString()
                                                        ));
                                                        moodAudios.add(newMoodAudio);
                                                    }
                                                });
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if (!CollectionUtils.isEmpty(moodAudios)) {
                                                mood.setMoodAudios(moodAudios);
                                                moods.add(mood);
                                            }

                                        } else {
                                            if (timbreMood.getFileName().toString().startsWith("avatar")) {
                                                List<String> avatarDir = new ArrayList<>(List.of(dirs));
                                                avatarDir.add(group.getFileName().toString());
                                                avatarDir.add(timbre.getFileName().toString());
                                                avatarDir.add(timbreMood.getFileName().toString());
                                                refAudio.setAvatar(pathConfig.buildFsUrl(avatarDir.toArray(new String[0])));
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (!CollectionUtils.isEmpty(moods)) {
                                refAudio.setMoods(moods);
                                refAudios.add(refAudio);
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        return refAudios;
    }

    public List<RefAudio> mergeAudioConfig(List<RefAudio> refAudios, List<RefAudio> refAudioConfigs) throws IOException {
        Map<String, RefAudio> audioMap = new HashMap<>();
        Map<String, List<String>> moodTagMap = new HashMap<>();
        for (RefAudio refAudioConfig : refAudioConfigs) {
            String key = refAudioConfig.getGroup() + "-" + refAudioConfig.getName();
            audioMap.put(key, refAudioConfig);
            for (RefAudio.Mood mood : refAudioConfig.getMoods()) {
                for (RefAudio.MoodAudio moodAudio : mood.getMoodAudios()) {
                    String key1 = key + "-" + mood.getName() + "-" + moodAudio.getName();
                    moodTagMap.put(key1, moodAudio.getTags());
                }
            }
        }

        Map<String, EdgeTtsVoice> edgeTtsVoiceMap = new HashMap<>();
        Path etConfigPath = Path.of(pathConfig.getFsDir(), "model", "edge-tts", "config.json");
        if (Files.exists(etConfigPath)) {
            EdgeTtsModelConfig edgeTtsModelConfig = JSON.parseObject(Files.readString(etConfigPath), EdgeTtsModelConfig.class);
            if (edgeTtsModelConfig != null && !CollectionUtils.isEmpty(edgeTtsModelConfig.getVoices())) {
                edgeTtsVoiceMap = edgeTtsModelConfig.getVoices().stream()
                        .collect(Collectors.toMap(v -> "edge-tts-" + v.getShortName(), Function.identity(), (v1, v2) -> v1));
            }
        }

        for (RefAudio refAudio : refAudios) {
            String key = refAudio.getGroup() + "-" + refAudio.getName();
            if (audioMap.containsKey(key)) {
                refAudio.setGender(audioMap.get(key).getGender());
                refAudio.setAgeGroup(audioMap.get(key).getAgeGroup());
                refAudio.setLanguage(audioMap.get(key).getLanguage());
                refAudio.setTags(audioMap.get(key).getTags());
            } else if (edgeTtsVoiceMap.containsKey(key)) {
                refAudio.setGender(convertEtGender(edgeTtsVoiceMap.get(key).getGender()));
                refAudio.setLanguage(convertEtLanguage(edgeTtsVoiceMap.get(key).getLocale()));
            }
            for (RefAudio.Mood mood : refAudio.getMoods()) {
                for (RefAudio.MoodAudio moodAudio : mood.getMoodAudios()) {
                    String key1 = key + "-" + mood.getName() + "-" + moodAudio.getName();
                    moodAudio.setTags(moodTagMap.get(key1));
                }
            }
        }

        return refAudios;
    }

    public static String convertEtGender(String gender) {
        return switch (gender) {
            case "Male" -> "男";
            case "Female" -> "女";
            default -> null;
        };
    }

    public static String convertEtLanguage(String language) {
        return switch (language.substring(0, language.indexOf("-"))) {
            case "zh" -> "中文";
            case "en" -> "英文";
            case "ja" -> "日文";
            case "ko" -> "韩文";
            default -> null;
        };
    }

}
