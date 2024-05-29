package space.wenliang.ai.aigcplatformserver.model.chat.qwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.model.chat.AiService;

public class QwenAiService implements AiService {

    @Value("${ai.qwen.api-key:}")
    private String apiKey;

    @Value("${ai.qwen.model:}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private Float temperature;

    @Override
    public Flux<String> call(String systemMessage, String userMessage) {
        try {
            Generation gen = new Generation();
            GenerationParam param = buildGenerationParam(systemMessage, userMessage, false);
            return Flux.just(gen.call(param).getOutput().getChoices().getFirst().getMessage().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Flux<String> stream(String systemMessage, String userMessage) {
        try {
            Generation gen = new Generation();
            GenerationParam param = buildGenerationParam(systemMessage, userMessage, true);
            Flowable<GenerationResult> result = gen.streamCall(param);
            return Flux.from(result).mapNotNull(generationResult -> generationResult.getOutput()
                    .getChoices().getFirst().getMessage().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GenerationParam buildGenerationParam(String systemMessage, String userMessage, boolean stream) {
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(systemMessage).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(userMessage).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        return GenerationParam.builder()
                .apiKey(apiKey)
                .model(model)
                .temperature(temperature)
                .messages(msgManager.get())
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(stream)
                .build();
    }
}
