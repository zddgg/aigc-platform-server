package space.wenliang.ai.aigcplatformserver.controller.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.model.RefAudio;
import space.wenliang.ai.aigcplatformserver.bean.model.RefAudioSort;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.ConfigService;
import space.wenliang.ai.aigcplatformserver.service.ModelService;
import space.wenliang.ai.aigcplatformserver.service.PathService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("model/refAudio")
public class RefAudioController {

    private final ModelService modelService;
    private final ConfigService configService;
    private final PathService pathService;

    public RefAudioController(ModelService modelService, ConfigService configService, PathService pathService) {
        this.modelService = modelService;
        this.configService = configService;
        this.pathService = pathService;
    }

    @PostMapping("queryRefAudios")
    public Result<Object> queryRefAudios() throws Exception {
        List<RefAudio> refAudios = modelService.getAudios();
        List<RefAudioSort> refAudioSorts = configService.getRefAudioSort();
        List<String> groupSort = refAudioSorts.stream().filter(r -> Objects.equals(r.getShowFlag(), Boolean.TRUE))
                .map(RefAudioSort::getGroup)
                .distinct()
                .toList();
        refAudios = refAudios.stream().filter(r -> groupSort.contains(r.getGroup()))
                .sorted(Comparator.comparingInt((RefAudio r) -> {
                    int index = groupSort.indexOf(r.getGroup());
                    return index < 0 ? Integer.MAX_VALUE : index;
                }))
                .toList();
        return Result.success(refAudios);
    }

    @PostMapping("updateRefAudio")
    public Result<Object> updateRefAudio(@RequestBody RefAudio refAudio) throws Exception {

        List<RefAudio> refAudioConfigs = configService.getRefAudioConfig();

        for (RefAudio refAudioConfig : refAudioConfigs) {
            if (StringUtils.equals(refAudio.getGroup() + "-" + refAudio.getName(),
                    refAudioConfig.getGroup() + "-" + refAudioConfig.getName())) {
                BeanUtils.copyProperties(refAudio, refAudioConfig);
            }
        }

        configService.saveRefAudioConfig(refAudioConfigs);
        return Result.success();
    }

    @PostMapping("queryRefAudioSort")
    public Result<Object> queryRefAudioSort() throws Exception {
        List<RefAudioSort> refAudioSorts = configService.getRefAudioSort();
        List<String> groupConfigList = refAudioSorts.stream().map(RefAudioSort::getGroup).toList();
        Path path = pathService.buildModelPath("ref-audio");
        if (Files.exists(path)) {
            List<String> groupList = Files.list(path).map(p -> p.getFileName().toString()).toList();
            List<RefAudioSort> newList = groupList.stream().filter(s -> !groupConfigList.contains(s))
                    .map(v -> {
                        RefAudioSort refAudioSort = new RefAudioSort();
                        refAudioSort.setGroup(v);
                        refAudioSort.setSortOrder(Integer.MAX_VALUE);
                        return refAudioSort;
                    })
                    .toList();
            refAudioSorts.addAll(newList);
            refAudioSorts.sort(Comparator.comparing(RefAudioSort::getSortOrder));
        }
        return Result.success(refAudioSorts);
    }


    @PostMapping("updateRefAudioSort")
    public Result<Object> updateRefAudioSort(@RequestBody List<RefAudioSort> refAudioSorts) throws Exception {
        configService.saveRefAudioSort(refAudioSorts);
        return Result.success(refAudioSorts);
    }
}
