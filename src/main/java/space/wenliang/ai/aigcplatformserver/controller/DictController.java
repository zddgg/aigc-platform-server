package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.LangDict;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.service.cache.DictService;

import java.util.List;

@RestController
@RequestMapping("dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @PostMapping("lang")
    public Result<Object> lang() {
        List<LangDict> langDicts = dictService.getLangDict();
        return Result.success(langDicts);
    }

    @PostMapping("lang/edit")
    public Result<Object> editLangDict(@RequestBody LangDict langDict) {
        dictService.editLangDict(langDict);
        return Result.success();
    }

    @PostMapping("lang/sort")
    public Result<Object> langDictSort(@RequestBody List<LangDict> langDicts) {
        dictService.langDictSort(langDicts);
        return Result.success();
    }
}
