package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.RoleInferenceParam;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;

@RestController
@RequestMapping("sse")
public class SseController {

    private final BTextChapterService bTextChapterService;

    public SseController(BTextChapterService bTextChapterService) {
        this.bTextChapterService = bTextChapterService;
    }

    @PostMapping("textChapter/roleInference")
    public Flux<String> roleInference(@RequestBody RoleInferenceParam roleInferenceParam) {
        return bTextChapterService.roleInference(roleInferenceParam);
    }
}
