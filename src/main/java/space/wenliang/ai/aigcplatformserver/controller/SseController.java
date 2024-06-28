package space.wenliang.ai.aigcplatformserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.io.IOException;

@RestController
@RequestMapping("sse")
public class SseController {

    private final BTextChapterService bTextChapterService;

    public SseController(BTextChapterService bTextChapterService) {
        this.bTextChapterService = bTextChapterService;
    }

    @PostMapping("textChapter/roleInference")
    public Flux<String> aiInference(@SingleValueParam("projectId") String projectId,
                                    @SingleValueParam("chapterId") String chapterId,
                                    HttpServletResponse response) {
        return bTextChapterService.roleInference(projectId, chapterId)
                .onErrorResume(e -> {
                    try {
                        String errorMessage;
                        if (e instanceof WebClientResponseException.Unauthorized) {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            errorMessage = "未经授权的访问，请检查 apiKey 等相关配置";
                        } else {
                            response.setStatus(HttpStatus.BAD_REQUEST.value());
                            errorMessage = "错误请求：" + e.getMessage();
                        }
                        response.setContentType("application/json; charset=UTF-8");
                        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
                        response.getWriter().flush();
                    } catch (IOException ioException) {
                        throw new RuntimeException(ioException);
                    }
                    return Flux.empty();
                });
    }
}
