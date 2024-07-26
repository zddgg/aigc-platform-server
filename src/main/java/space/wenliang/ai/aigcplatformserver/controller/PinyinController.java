package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.cache.CacheService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pinyin")
@RequiredArgsConstructor
public class PinyinController {

    private final CacheService cacheService;

    @PostMapping("getPinyinData")
    public Result<Object> getPinyinData() {
        Map<String, List<String>> textPinyins = cacheService.getTextPinyins();
        return Result.success(textPinyins);
    }
}
