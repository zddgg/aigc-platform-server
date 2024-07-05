package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.wenliang.ai.aigcplatformserver.bean.LangDict;
import space.wenliang.ai.aigcplatformserver.common.Result;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("dict")
public class DictController {

    @PostMapping("lang")
    public Result<Object> lang() {
        List<LangDict> langDicts = new ArrayList<>();
        langDicts.add(new LangDict("zh", "中文"));
        langDicts.add(new LangDict("en", "英文"));
        langDicts.add(new LangDict("ja", "日文"));
        langDicts.add(new LangDict("ko", "韩文"));
        return Result.success(langDicts);
    }
}
