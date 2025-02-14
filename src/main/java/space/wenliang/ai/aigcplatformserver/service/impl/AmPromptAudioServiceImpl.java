package space.wenliang.ai.aigcplatformserver.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudio;
import space.wenliang.ai.aigcplatformserver.bean.PromptAudioSort;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.AmPromptAudioEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.mapper.AmPromptAudioMapper;
import space.wenliang.ai.aigcplatformserver.service.AmPromptAudioService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;
import space.wenliang.ai.aigcplatformserver.util.KeyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static space.wenliang.ai.aigcplatformserver.common.CommonConstants.prompt_audio;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmPromptAudioServiceImpl extends ServiceImpl<AmPromptAudioMapper, AmPromptAudioEntity>
        implements AmPromptAudioService {

    private final EnvConfig envConfig;

    public static List<AmPromptAudioEntity> buildLocalPromptAudios(Path refAudioPath) throws IOException {
        Map<String, String> roleAvatarMap = new HashMap<>();
        Map<String, String> moodAvatarMap = new HashMap<>();
        List<AmPromptAudioEntity> promptAudios = new ArrayList<>();

        if (Files.exists(refAudioPath)) {
            try (Stream<Path> audioGroupPaths = Files.list(refAudioPath)) {
                audioGroupPaths.forEach(groupPath -> {
                    String group = groupPath.getFileName().toString();

                    if (Files.isDirectory(groupPath)) {
                        try (Stream<Path> rolePaths = Files.list(groupPath)) {
                            rolePaths.forEach(rolePath -> {
                                String role = rolePath.getFileName().toString();

                                if (Files.isDirectory(rolePath)) {
                                    try (Stream<Path> moodPaths = Files.list(rolePath)) {
                                        moodPaths.forEach(moodPath -> {
                                            String mood = moodPath.getFileName().toString();

                                            if (Files.isDirectory(moodPath)) {
                                                try (Stream<Path> audioPaths = Files.list(moodPath)) {
                                                    audioPaths.forEach(audioPath -> {
                                                        if (Files.isRegularFile(audioPath)) {
                                                            String audio = audioPath.getFileName().toString();

                                                            if (audio.startsWith("avatar")) {
                                                                moodAvatarMap.put(group + "-" + role + "-" + mood, audio);
                                                            } else if (audio.endsWith("wav")) {
                                                                AmPromptAudioEntity promptAudio = new AmPromptAudioEntity();
                                                                promptAudio.setPaGroup(group);
                                                                promptAudio.setPaRole(role);
                                                                promptAudio.setPaMood(mood);
                                                                promptAudio.setPaAudio(audio);
                                                                promptAudio.setPaAudioText(FileNameUtil.getPrefix(audio));
                                                                promptAudios.add(promptAudio);
                                                            }
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            } else if (Files.isRegularFile(moodPath) && mood.startsWith("avatar")) {
                                                roleAvatarMap.put(group + "-" + role, mood);
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

        return promptAudios.stream().peek(promptAudio -> {
            String key1 = promptAudio.getPaGroup() + "-" + promptAudio.getPaRole();
            String key2 = key1 + "-" + promptAudio.getPaMood();
            if (roleAvatarMap.containsKey(key1)) {
                promptAudio.setPaRoleAvatar(roleAvatarMap.get(key1));
            }
            if (moodAvatarMap.containsKey(key2)) {
                promptAudio.setPaMoodAvatar(moodAvatarMap.get(key2));
            }
        }).toList();
    }

    @Override
    public AmPromptAudioEntity getByPaId(String paId) {
        return this.getOne(new LambdaQueryWrapper<AmPromptAudioEntity>().eq(AmPromptAudioEntity::getPaId, paId));
    }

    @Override
    public void refreshCache() {
        List<AmPromptAudioEntity> cachePromptAudios = this.list();
        List<PromptAudioSort> promptAudioSorts = queryPromptAudioSorts();

        try {
            List<AmPromptAudioEntity> localPromptAudios = buildLocalPromptAudios(envConfig.buildModelPath(prompt_audio));

            mergePromptAudios(localPromptAudios, cachePromptAudios, promptAudioSorts);

            log.info("参考音频缓存刷新成功");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException("参考音频缓存刷新失败");
        }
    }

    @Override
    public List<PromptAudioSort> queryPromptAudioSorts() {
        return this.list()
                .stream()
                .collect(Collectors.toMap(AmPromptAudioEntity::getPaGroup, Function.identity(), (a, b) -> b))
                .values()
                .stream()
                .map(refAudioEntity -> {
                    PromptAudioSort refAudioSort = new PromptAudioSort();
                    refAudioSort.setGroup(refAudioEntity.getPaGroup());
                    refAudioSort.setSortOrder(Optional.ofNullable(refAudioEntity.getPaGroupSort()).orElse(0));
                    refAudioSort.setShowFlag(refAudioEntity.getPaGroupShow());
                    return refAudioSort;
                })
                .sorted(Comparator.comparingInt(PromptAudioSort::getSortOrder))
                .toList();
    }

    @Override
    public List<PromptAudio> promptAudios() {
        List<String> promptAudioSorts = queryPromptAudioSorts()
                .stream()
                .map(PromptAudioSort::getGroup)
                .toList();

        List<AmPromptAudioEntity> promptAudioEntities = this.list(
                new LambdaQueryWrapper<AmPromptAudioEntity>()
                        .eq(AmPromptAudioEntity::getPaGroupShow, true));

        Map<String, Map<String, List<AmPromptAudioEntity>>> groupedMap = promptAudioEntities
                .stream()
                .collect(Collectors.groupingBy(r ->
                                String.join("-", List.of(r.getPaGroup(), r.getPaRole())),
                        Collectors.groupingBy(AmPromptAudioEntity::getPaMood)));

        Map<String, AmPromptAudioEntity> audioNamesMap = promptAudioEntities.stream()
                .collect(Collectors.toMap(r ->
                        String.join("-", List.of(r.getPaGroup(), r.getPaRole())), Function.identity(), (a, b) -> b));

        Map<String, List<AmPromptAudioEntity>> audioMoodsMap = promptAudioEntities.stream()
                .collect(Collectors.groupingBy(r ->
                        String.join("-", List.of(r.getPaGroup(), r.getPaRole(), r.getPaMood()))));


        return groupedMap.entrySet()
                .stream()
                .map(entry -> {
                    AmPromptAudioEntity promptAudioEntity = audioNamesMap.get(entry.getKey());

                    PromptAudio promptAudio = new PromptAudio();
                    promptAudio.setPaGroup(promptAudioEntity.getPaGroup());
                    promptAudio.setPaRole(promptAudioEntity.getPaRole());
                    promptAudio.setPaRoleGender(promptAudioEntity.getPaRoleGender());
                    promptAudio.setPaRoleAge(promptAudioEntity.getPaRoleAge());
                    promptAudio.setPaRoleLang(promptAudioEntity.getPaRoleLang());
                    promptAudio.setPaRoleTags(StringUtils.isBlank(promptAudioEntity.getPaRoleTags()) ? List.of() : List.of(promptAudioEntity.getPaRoleTags().split(",")));

                    if (StringUtils.isNotBlank(promptAudioEntity.getPaRoleAvatar())) {
                        promptAudio.setPaRoleAvatar(envConfig.buildModelUrl(
                                prompt_audio,
                                promptAudioEntity.getPaGroup(),
                                promptAudioEntity.getPaRole(),
                                promptAudioEntity.getPaRoleAvatar()
                        ));
                    }


                    List<PromptAudio.PaMood> paMoods = entry.getValue()
                            .entrySet()
                            .stream()
                            .map(entry1 -> {
                                AmPromptAudioEntity moodEntity = audioMoodsMap
                                        .get(String.join("-",
                                                List.of(promptAudioEntity.getPaGroup(), promptAudioEntity.getPaRole(), entry1.getKey()))).getFirst();

                                PromptAudio.PaMood paMood = new PromptAudio.PaMood();
                                paMood.setPaMood(moodEntity.getPaMood());

                                if (StringUtils.isNotBlank(moodEntity.getPaMoodAvatar())) {
                                    paMood.setPaMoodAvatar(envConfig.buildModelUrl(
                                            prompt_audio,
                                            moodEntity.getPaGroup(),
                                            moodEntity.getPaRole(),
                                            moodEntity.getPaMood(),
                                            moodEntity.getPaMoodAvatar()
                                    ));
                                }

                                List<PromptAudio.PaAudio> paAudios = entry1.getValue()
                                        .stream()
                                        .map(r1 -> {
                                            PromptAudio.PaAudio paAudio = new PromptAudio.PaAudio();
                                            paAudio.setId(r1.getId());
                                            paAudio.setPaId(r1.getPaId());
                                            paAudio.setPaAudio(r1.getPaAudio());
                                            paAudio.setPaAudioText(r1.getPaAudioText());
                                            paAudio.setPaAudioLang(r1.getPaAudioLang());
                                            paAudio.setPaAudioTags(StringUtils.isBlank(r1.getPaAudioTags()) ? List.of() : List.of(r1.getPaAudioTags().split(",")));
                                            paAudio.setAudioUrl(envConfig.buildModelUrl(
                                                    prompt_audio,
                                                    moodEntity.getPaGroup(),
                                                    moodEntity.getPaRole(),
                                                    moodEntity.getPaMood(),
                                                    paAudio.getPaAudio()
                                            ));
                                            return paAudio;
                                        }).toList();

                                paMood.setPaAudios(paAudios);
                                return paMood;
                            }).toList();
                    promptAudio.setPaMoods(paMoods);

                    return promptAudio;
                })
                .sorted(Comparator.comparing(r -> {
                    if (!promptAudioSorts.contains(r.getPaGroup())) {
                        return Integer.MAX_VALUE;
                    }
                    return promptAudioSorts.indexOf(r.getPaGroup());
                }))
                .toList();
    }

    @Override
    public void updatePromptAudio(PromptAudio promptAudio) {
        this.update(new LambdaUpdateWrapper<AmPromptAudioEntity>()
                .eq(AmPromptAudioEntity::getPaGroup, promptAudio.getPaGroup())
                .eq(AmPromptAudioEntity::getPaRole, promptAudio.getPaRole())
                .set(AmPromptAudioEntity::getPaRoleGender, promptAudio.getPaRoleGender())
                .set(AmPromptAudioEntity::getPaRoleAge, promptAudio.getPaRoleAge())
                .set(AmPromptAudioEntity::getPaRoleLang, promptAudio.getPaRoleLang())
                .set(AmPromptAudioEntity::getPaRoleTags, String.join(",", promptAudio.getPaRoleTags())));

        promptAudio.getPaMoods().forEach(mood ->
                mood.getPaAudios().forEach(moodAudio ->
                        this.update(new LambdaUpdateWrapper<AmPromptAudioEntity>()
                                .eq(AmPromptAudioEntity::getPaGroup, promptAudio.getPaGroup())
                                .eq(AmPromptAudioEntity::getPaRole, promptAudio.getPaRole())
                                .eq(AmPromptAudioEntity::getPaMood, mood.getPaMood())
                                .eq(AmPromptAudioEntity::getPaAudio, moodAudio.getPaAudio())
                                .set(AmPromptAudioEntity::getPaAudioTags, String.join(",", moodAudio.getPaAudioTags())))));

    }

    @Override
    public void updatePromptAudioSorts(List<PromptAudioSort> promptAudioSorts) {
        for (PromptAudioSort promptAudioSort : promptAudioSorts) {
            this.update(new LambdaUpdateWrapper<AmPromptAudioEntity>()
                    .eq(AmPromptAudioEntity::getPaGroup, promptAudioSort.getGroup())
                    .set(AmPromptAudioEntity::getPaGroupSort, promptAudioSort.getSortOrder())
                    .set(AmPromptAudioEntity::getPaGroupShow, promptAudioSort.getShowFlag()));
        }
    }

    public void mergePromptAudios(List<AmPromptAudioEntity> localPromptAudios,
                                  List<AmPromptAudioEntity> cachePromptAudios,
                                  List<PromptAudioSort> promptAudioSorts) {

        Map<String, AmPromptAudioEntity> localModelMap = localPromptAudios.stream()
                .collect(Collectors.toMap(this::buildDupKey, Function.identity(), (a, b) -> b));

        Map<String, AmPromptAudioEntity> cacheModelMap = cachePromptAudios.stream()
                .collect(Collectors.toMap(this::buildDupKey, Function.identity(), (a, b) -> b));

        Map<String, PromptAudioSort> audioSortMap = promptAudioSorts.stream()
                .collect(Collectors.toMap(PromptAudioSort::getGroup, Function.identity(), (a, b) -> b));

        List<AmPromptAudioEntity> updatePromptAudios = new ArrayList<>();
        List<Integer> removeIds = new ArrayList<>();

        for (AmPromptAudioEntity promptAudio : cachePromptAudios) {
            String key = buildDupKey(promptAudio);
            if (localModelMap.containsKey(key)) {
                AmPromptAudioEntity local = localModelMap.get(key);
                promptAudio.setPaRoleAvatar(local.getPaRoleAvatar());
                promptAudio.setPaMoodAvatar(local.getPaMoodAvatar());

                updatePromptAudios.add(promptAudio);
            } else {
                removeIds.add(promptAudio.getId());
            }
        }

        List<AmPromptAudioEntity> newPromptAudios = localPromptAudios.stream()
                .filter(modelFile -> !cacheModelMap.containsKey(buildDupKey(modelFile)))
                .peek(v -> {
                    v.setPaId(IdUtils.uuid());
                    if (audioSortMap.containsKey(v.getPaGroup())) {
                        v.setPaGroupSort(audioSortMap.get(v.getPaGroup()).getSortOrder());
                        v.setPaGroupShow(audioSortMap.get(v.getPaGroup()).getShowFlag());
                    }
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

    public String buildDupKey(AmPromptAudioEntity promptAudio) {
        return KeyUtils.combineKey(promptAudio.getPaGroup(), promptAudio.getPaRole(), promptAudio.getPaMood(), promptAudio.getPaAudio());
    }

}




