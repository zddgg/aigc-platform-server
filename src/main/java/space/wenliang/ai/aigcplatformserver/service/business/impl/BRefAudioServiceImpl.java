package space.wenliang.ai.aigcplatformserver.service.business.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.RefAudio;
import space.wenliang.ai.aigcplatformserver.bean.RefAudioSort;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.EdgeTtsConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.AEdgeTtsConfigService;
import space.wenliang.ai.aigcplatformserver.service.application.ARefAudioService;
import space.wenliang.ai.aigcplatformserver.service.business.BRefAudioService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class BRefAudioServiceImpl implements BRefAudioService {

    private final PathConfig pathConfig;
    private final ARefAudioService aRefAudioService;
    private final AEdgeTtsConfigService aEdgeTtsConfigService;

    public BRefAudioServiceImpl(PathConfig pathConfig, ARefAudioService aRefAudioService, AEdgeTtsConfigService aEdgeTtsConfigService) {
        this.pathConfig = pathConfig;
        this.aRefAudioService = aRefAudioService;
        this.aEdgeTtsConfigService = aEdgeTtsConfigService;
    }

    @Override
    public List<RefAudio> refAudioList() {
        List<String> refAudioSorts = queryGroupSorts()
                .stream()
                .map(RefAudioSort::getGroup)
                .toList();

        List<RefAudioEntity> refAudioEntities = aRefAudioService.list();

        Map<String, Map<String, List<RefAudioEntity>>> groupedMap = refAudioEntities
                .stream()
                .collect(Collectors.groupingBy(r ->
                                String.join("-", List.of(r.getAudioGroup(), r.getAudioName())),
                        Collectors.groupingBy(RefAudioEntity::getMoodName)));

        Map<String, RefAudioEntity> audioNamesMap = refAudioEntities.stream()
                .collect(Collectors.toMap(r ->
                        String.join("-", List.of(r.getAudioGroup(), r.getAudioName())), Function.identity(), (_, b) -> b));

        Map<String, List<RefAudioEntity>> audioMoodsMap = refAudioEntities.stream()
                .collect(Collectors.groupingBy(r ->
                        String.join("-", List.of(r.getAudioGroup(), r.getAudioName(), r.getMoodName()))));


        return groupedMap.entrySet()
                .stream()
                .map(entry -> {
                    RefAudioEntity refAudioEntity = audioNamesMap.get(entry.getKey());

                    RefAudio refAudio = new RefAudio();
                    refAudio.setName(refAudioEntity.getAudioName());
                    refAudio.setGroup(refAudioEntity.getAudioGroup());
                    refAudio.setGender(refAudioEntity.getGender());
                    refAudio.setAgeGroup(refAudioEntity.getAgeGroup());
                    refAudio.setLanguage(refAudioEntity.getLanguage());

                    if (StringUtils.isNotBlank(refAudioEntity.getAvatar())) {
                        refAudio.setAvatarUrl(pathConfig.buildModelUrl(
                                "ref-audio",
                                refAudioEntity.getAudioGroup(),
                                refAudioEntity.getAudioName(),
                                refAudioEntity.getAvatar()
                        ));
                    }

                    refAudio.setTags(StringUtils.isBlank(refAudioEntity.getTags()) ? List.of() : List.of(refAudioEntity.getTags().split(",")));

                    List<RefAudio.Mood> moods = entry.getValue()
                            .entrySet()
                            .stream()
                            .map(entry1 -> {
                                RefAudioEntity moodEntity = audioMoodsMap
                                        .get(String.join("-",
                                                List.of(refAudioEntity.getAudioGroup(), refAudioEntity.getAudioName(), entry1.getKey()))).getFirst();

                                RefAudio.Mood mood = new RefAudio.Mood();
                                mood.setName(moodEntity.getMoodName());

                                if (StringUtils.isNotBlank(moodEntity.getMoodAvatar())) {
                                    mood.setAvatarUrl(pathConfig.buildModelUrl(
                                            "ref-audio",
                                            moodEntity.getAudioGroup(),
                                            moodEntity.getAudioName(),
                                            moodEntity.getMoodName(),
                                            moodEntity.getMoodAvatar()
                                    ));
                                }

                                List<RefAudio.MoodAudio> moodAudios = entry1.getValue()
                                        .stream()
                                        .map(r1 -> {
                                            RefAudio.MoodAudio moodAudio = new RefAudio.MoodAudio();
                                            moodAudio.setId(r1.getRefAudioId());
                                            moodAudio.setName(r1.getMoodAudioName());
                                            moodAudio.setText(r1.getMoodAudioText());
                                            moodAudio.setTags(StringUtils.isBlank(r1.getMoodAudioTags()) ? List.of() : List.of(r1.getMoodAudioTags().split(",")));
                                            moodAudio.setAudioUrl(pathConfig.buildModelUrl(
                                                    "ref-audio",
                                                    moodEntity.getAudioGroup(),
                                                    moodEntity.getAudioName(),
                                                    moodEntity.getMoodName(),
                                                    moodAudio.getName()
                                            ));
                                            return moodAudio;
                                        }).toList();

                                mood.setMoodAudios(moodAudios);
                                return mood;
                            }).toList();
                    refAudio.setMoods(moods);

                    return refAudio;
                })
                .sorted(Comparator.comparing(r -> {
                    if (!refAudioSorts.contains(r.getGroup())) {
                        return Integer.MAX_VALUE;
                    }
                    return refAudioSorts.indexOf(r.getGroup());
                }))
                .toList();
    }

    @Override
    public void refreshCache() {
        List<RefAudioEntity> cacheRefAudioConfigs = aRefAudioService.allList();
        try {
            List<RefAudioEntity> localRefAudios = buildLocalRefAudios(pathConfig.buildModelPath("ref-audio"));

            List<RefAudioEntity> mergeRefAudioEntities = mergeAudioConfigs(localRefAudios, cacheRefAudioConfigs);

            aRefAudioService.remove(null);
            aRefAudioService.saveBatch(mergeRefAudioEntities);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException("本地参考音频文件读取失败");
        }
    }

    @Override
    public void updateRefAudio(RefAudio refAudio) {
        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .eq(RefAudioEntity::getAudioGroup, refAudio.getGroup())
                .eq(RefAudioEntity::getAudioName, refAudio.getName())
                .set(RefAudioEntity::getGender, refAudio.getGender())
                .set(RefAudioEntity::getAgeGroup, refAudio.getAgeGroup())
                .set(RefAudioEntity::getLanguage, refAudio.getLanguage())
                .set(RefAudioEntity::getTags, String.join(",", refAudio.getTags())));

        refAudio.getMoods().forEach(mood ->
                mood.getMoodAudios().forEach(moodAudio ->
                        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                                .eq(RefAudioEntity::getAudioGroup, refAudio.getGroup())
                                .eq(RefAudioEntity::getAudioName, refAudio.getName())
                                .eq(RefAudioEntity::getMoodName, mood.getName())
                                .eq(RefAudioEntity::getMoodAudioName, moodAudio.getName())
                                .set(RefAudioEntity::getMoodAudioTags, String.join(",", moodAudio.getTags())))));
    }

    @Override
    public List<RefAudioSort> queryGroupSorts() {
        return aRefAudioService.allList()
                .stream()
                .collect(Collectors.toMap(RefAudioEntity::getAudioGroup, Function.identity(), (_, b) -> b))
                .values()
                .stream()
                .map(refAudioEntity -> {
                    RefAudioSort refAudioSort = new RefAudioSort();
                    refAudioSort.setGroup(refAudioEntity.getAudioGroup());
                    refAudioSort.setSortOrder(Optional.ofNullable(refAudioEntity.getGroupSortOrder()).orElse(0));
                    refAudioSort.setShowFlag(refAudioEntity.getGroupShowFlag());
                    return refAudioSort;
                })
                .sorted(Comparator.comparingInt(RefAudioSort::getSortOrder))
                .toList();
    }

    @Override
    public void updateRefAudioSorts(List<RefAudioSort> refAudioSorts) {
        for (RefAudioSort refAudioSort : refAudioSorts) {
            aRefAudioService.updateRefAudioSort(refAudioSort);
        }
    }

    public static List<RefAudioEntity> buildLocalRefAudios(Path refAudioPath) throws IOException {
        Map<String, String> refAudioAvatarMap = new HashMap<>();
        Map<String, String> refAudioMoodAvatarMap = new HashMap<>();
        List<RefAudioEntity> refAudioEntities = new ArrayList<>();

        if (Files.exists(refAudioPath)) {
            try (Stream<Path> audioGroupPaths = Files.list(refAudioPath)) {
                audioGroupPaths.forEach(audioGroupPath -> {
                    String audioGroup = audioGroupPath.getFileName().toString();

                    if (Files.isDirectory(audioGroupPath)) {
                        try (Stream<Path> audioPaths = Files.list(audioGroupPath)) {
                            audioPaths.forEach(audioPath -> {
                                String audioName = audioPath.getFileName().toString();

                                if (Files.isDirectory(audioPath)) {
                                    try (Stream<Path> moodPaths = Files.list(audioPath)) {
                                        moodPaths.forEach(moodPath -> {
                                            String moodName = moodPath.getFileName().toString();

                                            if (Files.isDirectory(moodPath)) {
                                                try (Stream<Path> moodAudios = Files.list(moodPath)) {
                                                    moodAudios.forEach(moodAudio -> {
                                                        if (Files.isRegularFile(moodAudio)) {
                                                            String moodAudioName = moodAudio.getFileName().toString();

                                                            if (moodAudioName.startsWith("avatar")) {
                                                                refAudioMoodAvatarMap.put(audioGroup + "-" + audioName + "-" + moodName, moodAudioName);
                                                            } else if (moodAudioName.endsWith("wav")) {
                                                                RefAudioEntity refAudioEntity = new RefAudioEntity();
                                                                refAudioEntity.setAudioGroup(audioGroup);
                                                                refAudioEntity.setAudioName(audioName);
                                                                refAudioEntity.setMoodName(moodName);
                                                                refAudioEntity.setMoodAudioName(moodAudioName);
                                                                refAudioEntity.setMoodAudioText(FileNameUtil.getPrefix(moodAudioName));
                                                                refAudioEntities.add(refAudioEntity);
                                                            }
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            } else if (Files.isRegularFile(moodPath) && moodName.startsWith("avatar")) {
                                                refAudioAvatarMap.put(audioGroup + "-" + audioName, moodName);
                                            }
                                        });
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

        return refAudioEntities.stream().peek(refAudioEntity -> {
            String key1 = refAudioEntity.getAudioGroup() + "-" + refAudioEntity.getAudioName();
            String key2 = key1 + "-" + refAudioEntity.getMoodName();
            if (refAudioAvatarMap.containsKey(key1)) {
                refAudioEntity.setAvatar(refAudioAvatarMap.get(key1));
            }
            if (refAudioMoodAvatarMap.containsKey(key2)) {
                refAudioEntity.setMoodAvatar(refAudioMoodAvatarMap.get(key2));
            }
        }).toList();
    }

    public List<RefAudioEntity> mergeAudioConfigs(List<RefAudioEntity> localRefAudios, List<RefAudioEntity> cacheRefAudioConfigs) throws IOException {
        Map<String, RefAudioEntity> refAudioMap = new HashMap<>();

        Map<String, RefAudioEntity> moodAudioTagsMap = new HashMap<>();

        Map<String, Boolean> groupShowFlagMap = new HashMap<>();


        for (RefAudioEntity refAudioEntity : cacheRefAudioConfigs) {
            String key = refAudioEntity.getAudioGroup() + "-" + refAudioEntity.getAudioName();
            refAudioMap.put(key, refAudioEntity);

            String key1 = key + "-" + refAudioEntity.getMoodName() + "-" + refAudioEntity.getMoodAudioName();
            moodAudioTagsMap.put(key1, refAudioEntity);

            if (Objects.equals(refAudioEntity.getGroupShowFlag(), Boolean.TRUE)) {
                groupShowFlagMap.put(refAudioEntity.getAudioGroup(), Boolean.TRUE);
            }
        }

        Map<String, EdgeTtsConfigEntity> edgeTtsVoiceMap = aEdgeTtsConfigService.list()
                .stream()
                .collect(Collectors.toMap(EdgeTtsConfigEntity::getShortName, Function.identity(), (v1, _) -> v1));


        for (RefAudioEntity refAudioEntity : localRefAudios) {
            String key = refAudioEntity.getAudioGroup() + "-" + refAudioEntity.getAudioName();

            if (refAudioMap.containsKey(key)) {
                refAudioEntity.setGroupSortOrder(refAudioMap.get(key).getGroupSortOrder());
                refAudioEntity.setGender(refAudioMap.get(key).getGender());
                refAudioEntity.setAgeGroup(refAudioMap.get(key).getAgeGroup());
                refAudioEntity.setLanguage(refAudioMap.get(key).getLanguage());
                refAudioEntity.setTags(refAudioMap.get(key).getTags());
            } else if (StringUtils.equals(refAudioEntity.getAudioGroup(), "edge-tts")
                    && edgeTtsVoiceMap.containsKey(refAudioEntity.getAudioGroup())) {
                refAudioEntity.setGender(convertEtGender(edgeTtsVoiceMap.get(refAudioEntity.getAudioGroup()).getGender()));
                refAudioEntity.setLanguage(convertEtLanguage(edgeTtsVoiceMap.get(refAudioEntity.getAudioGroup()).getLocale()));
            }

            String key1 = key + "-" + refAudioEntity.getMoodName() + "-" + refAudioEntity.getMoodAudioName();
            if (moodAudioTagsMap.containsKey(key1)) {
                refAudioEntity.setRefAudioId(moodAudioTagsMap.get(key1).getRefAudioId());
                refAudioEntity.setMoodAudioTags(moodAudioTagsMap.get(key1).getMoodAudioTags());
            } else {
                refAudioEntity.setRefAudioId(IdUtils.uuid());
            }

            if (groupShowFlagMap.containsKey(refAudioEntity.getAudioGroup())) {
                refAudioEntity.setGroupShowFlag(groupShowFlagMap.get(refAudioEntity.getAudioGroup()));
            }
        }

        return localRefAudios;
    }

    public static String convertEtGender(String gender) {
        return switch (gender) {
            case "Male" -> "男";
            case "Female" -> "女";
            default -> gender;
        };
    }

    public static String convertEtLanguage(String language) {
        String lang = language.substring(0, language.indexOf("-"));
        return switch (lang) {
            case "zh" -> "中文";
            case "en" -> "英文";
            case "ja" -> "日文";
            case "ko" -> "韩文";
            default -> lang;
        };
    }
}
