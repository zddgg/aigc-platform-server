package space.wenliang.ai.aigcplatformserver.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.common.ModelTypeEnum;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.AmModelFileEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.mapper.AmModelFileMapper;
import space.wenliang.ai.aigcplatformserver.service.AmModelFileService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;
import space.wenliang.ai.aigcplatformserver.util.KeyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmModelFileServiceImpl extends ServiceImpl<AmModelFileMapper, AmModelFileEntity>
        implements AmModelFileService {

    private final EnvConfig envConfig;

    @Override
    public AmModelFileEntity getByMfId(String mfId) {
        return this.getOne(new LambdaQueryWrapper<AmModelFileEntity>().eq(AmModelFileEntity::getMfId, mfId));
    }

    @Override
    public void refreshCache(String modelType) {
        List<AmModelFileEntity> cacheModelFiles = this.list();

        try {
            List<AmModelFileEntity> localModelFiles = buildLocalModelFiles(modelType);

            mergeModelFiles(localModelFiles, cacheModelFiles);

            log.info("音频模型文件缓存刷新成功");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException("音频模型文件缓存刷新失败");
        }
    }

    @Override
    public List<AmModelFileEntity> getByModelType(String modelType) {
        return this.list(new LambdaQueryWrapper<AmModelFileEntity>()
                .eq(AmModelFileEntity::getAmType, modelType));
    }

    public void mergeModelFiles(List<AmModelFileEntity> localModelFiles, List<AmModelFileEntity> cacheModelFiles) {

        Map<String, AmModelFileEntity> localModelMap = localModelFiles.stream()
                .collect(Collectors.toMap(this::buildDupKey, Function.identity(), (_, b) -> b));

        Map<String, AmModelFileEntity> cacheModelMap = cacheModelFiles.stream()
                .collect(Collectors.toMap(this::buildDupKey, Function.identity(), (_, b) -> b));

        List<AmModelFileEntity> updateModelFiles = new ArrayList<>();
        List<Integer> removeIds = new ArrayList<>();

        for (AmModelFileEntity modelFile : cacheModelFiles) {
            String key = buildDupKey(modelFile);
            if (localModelMap.containsKey(key)) {
                modelFile.setMfJson(localModelMap.get(key).getMfJson());
                updateModelFiles.add(modelFile);
            } else {
                removeIds.add(modelFile.getId());
            }
        }

        List<AmModelFileEntity> newModelFiles = localModelFiles.stream()
                .filter(modelFile -> !cacheModelMap.containsKey(buildDupKey(modelFile)))
                .peek(v -> v.setMfId(IdUtils.uuid()))
                .sorted(Comparator.comparing(AmModelFileEntity::getMfRole))
                .toList();

        if (!CollectionUtils.isEmpty(updateModelFiles)) {
            this.updateBatchById(updateModelFiles);
        }

        if (!CollectionUtils.isEmpty(removeIds)) {
            this.removeByIds(removeIds);
        }

        if (!CollectionUtils.isEmpty(newModelFiles)) {
            this.saveBatch(newModelFiles);
        }
    }

    private List<AmModelFileEntity> buildLocalModelFiles(String... modelTypes) throws IOException {
        List<AmModelFileEntity> modelFiles = new ArrayList<>();

        for (String modelType : modelTypes) {

            Path modelTypePath = envConfig.buildModelPath(modelType);

            if (Files.exists(modelTypePath) && Files.isDirectory(modelTypePath)) {
                try (Stream<Path> groupPaths = Files.list(modelTypePath)) {
                    groupPaths.forEach(groupPath -> {


                        if (Files.isDirectory(groupPath)) {
                            try (Stream<Path> rolePaths = Files.list(groupPath)) {
                                rolePaths.forEach(rolePath -> {
                                    String group = groupPath.getFileName().toString();
                                    String role = rolePath.getFileName().toString();

                                    AmModelFileEntity amModelFileEntity = new AmModelFileEntity();
                                    amModelFileEntity.setAmType(modelType);
                                    amModelFileEntity.setMfGroup(group);
                                    amModelFileEntity.setMfRole(role);

                                    List<Map<String, String>> files = new ArrayList<>();
                                    if (Files.isDirectory(rolePath)) {
                                        if (StringUtils.equals(modelType, ModelTypeEnum.gpt_sovits.getName())) {
                                            try (Stream<Path> filenames = Files.list(rolePath)) {
                                                filenames.forEach(file -> {

                                                    if (Files.isRegularFile(file)) {

                                                        if (file.getFileName().toString().endsWith(".ckpt")) {
                                                            files.add(Map.of("fileType", "ckpt", "fileName", file.getFileName().toString()));
                                                        }
                                                        if (file.getFileName().toString().endsWith(".pth")) {
                                                            files.add(Map.of("fileType", "pth", "fileName", file.getFileName().toString()));
                                                        }
                                                    }
                                                });
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                    }

                                    amModelFileEntity.setMfJson(JSON.toJSONString(files));

                                    modelFiles.add(amModelFileEntity);
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

        }
        return modelFiles;
    }

    public String buildDupKey(AmModelFileEntity modelFile) {
        return KeyUtils.combineKey(modelFile.getAmType(), modelFile.getMfGroup(), modelFile.getMfRole());
    }
}




