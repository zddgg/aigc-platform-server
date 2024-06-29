package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

@RestController
@RequestMapping("sse")
public class SseController {

    private final BTextChapterService bTextChapterService;
    private final GlobalWebSocketHandler globalWebSocketHandler;

    public SseController(BTextChapterService bTextChapterService,
                         GlobalWebSocketHandler globalWebSocketHandler) {
        this.bTextChapterService = bTextChapterService;
        this.globalWebSocketHandler = globalWebSocketHandler;
    }

    @PostMapping("textChapter/roleInference")
    public Flux<String> aiInference(@SingleValueParam("projectId") String projectId,
                                    @SingleValueParam("chapterId") String chapterId) {
        return bTextChapterService.roleInference(projectId, chapterId)
                .onErrorResume(e -> {
                    String errorMessage;
                    if (e instanceof WebClientResponseException.Unauthorized) {
                        errorMessage = "未经授权的访问，请检查 apiKey 等相关配置";
                    } else {
                        errorMessage = "错误请求：" + e.getMessage();
                    }
                    globalWebSocketHandler.sendErrorMessage(errorMessage);
                    return Flux.empty();
                });
    }
}
