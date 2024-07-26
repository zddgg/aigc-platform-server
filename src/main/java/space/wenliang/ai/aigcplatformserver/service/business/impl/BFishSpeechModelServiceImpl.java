package space.wenliang.ai.aigcplatformserver.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.config.PathConfig;
import space.wenliang.ai.aigcplatformserver.entity.FishSpeechModelEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.application.AFishSpeechModelService;
import space.wenliang.ai.aigcplatformserver.service.business.BFishSpeechModelService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BFishSpeechModelServiceImpl implements BFishSpeechModelService {

    private final PathConfig pathConfig;
    private final AFishSpeechModelService aFishSpeechModelService;

    @Override
    public List<FishSpeechModelEntity> list() {
        return aFishSpeechModelService.list()
                .stream()
                .sorted(Comparator.comparing(v -> !StringUtils.equals(v.getModelGroup(), "默认")))
                .toList();
    }

    @Override
    public void refreshCache() {
        List<FishSpeechModelEntity> cacheModels = aFishSpeechModelService.list();
        try {
            List<FishSpeechModelEntity> localModels = buildLocalModels(pathConfig.buildModelPath("fish-speech"));

            List<FishSpeechModelEntity> mergeModelEntities = mergeModels(localModels, cacheModels);

            aFishSpeechModelService.remove(null);
            aFishSpeechModelService.saveBatch(mergeModelEntities);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BizException("本地参考音频文件读取失败");
        }
    }

    private List<FishSpeechModelEntity> mergeModels(List<FishSpeechModelEntity> localModels, List<FishSpeechModelEntity> cacheModels) {

        Map<String, FishSpeechModelEntity> modelMap = cacheModels.stream()
                .collect(Collectors.toMap(v -> v.getModelGroup() + "-" + v.getModelName(), v -> v, (_, b) -> b));

        return localModels.stream()
                .peek(v -> {
                    if (modelMap.containsKey(v.getModelGroup() + "-" + v.getModelName())) {
                        v.setModelId(modelMap.get(v.getModelGroup() + "-" + v.getModelName()).getModelId());
                    } else {
                        v.setModelId(IdUtils.uuid());
                    }
                }).toList();
    }

    private List<FishSpeechModelEntity> buildLocalModels(Path modelPath) throws IOException {
        List<FishSpeechModelEntity> modelEntities = new ArrayList<>();

        if (Files.exists(modelPath)) {
            try (Stream<Path> audioGroupPaths = Files.list(modelPath)) {
                audioGroupPaths.forEach(groupPath -> {
                    String group = groupPath.getFileName().toString();

                    if (Files.isDirectory(groupPath)) {
                        try (Stream<Path> namePaths = Files.list(groupPath)) {
                            namePaths.forEach(namePath -> {
                                FishSpeechModelEntity modelEntity = new FishSpeechModelEntity();
                                String name = namePath.getFileName().toString();
                                modelEntity.setModelGroup(group);
                                modelEntity.setModelName(name);

                                if (Files.isDirectory(namePath)) {
                                    try (Stream<Path> fileNamePaths = Files.list(namePath)) {
                                        fileNamePaths.forEach(fileNamePath -> {
                                            if (fileNamePath.getFileName().toString().endsWith(".ckpt")) {
                                                modelEntity.setCkpt(fileNamePath.getFileName().toString());
                                            }
                                            if (fileNamePath.getFileName().toString().endsWith(".pth")) {
                                                modelEntity.setPth(fileNamePath.getFileName().toString());
                                            }
                                        });
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                modelEntities.add(modelEntity);
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

        return modelEntities;
    }
}
