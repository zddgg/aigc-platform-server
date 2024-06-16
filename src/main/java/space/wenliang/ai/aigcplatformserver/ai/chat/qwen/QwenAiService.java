package space.wenliang.ai.aigcplatformserver.ai.chat.qwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.ai.chat.IAiService;
import space.wenliang.ai.aigcplatformserver.entity.ChatModelConfigEntity;

@Slf4j
@Service("Qwen")
public class QwenAiService implements IAiService {

    @Override
    public Flux<String> call(ChatModelConfigEntity config, String systemMessage, String userMessage) {
        try {
            Generation gen = new Generation();
            GenerationParam generationParam = buildGenerationParam(config, systemMessage, userMessage, false);
            return Flux.just(gen.call(generationParam).getOutput().getChoices().getFirst().getMessage().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Flux<String> stream(ChatModelConfigEntity config, String systemMessage, String userMessage) {
        try {
            Generation gen = new Generation();
            GenerationParam generationParam = buildGenerationParam(config, systemMessage, userMessage, true);
            Flowable<GenerationResult> result = gen.streamCall(generationParam);
            return Flux.from(result).mapNotNull(generationResult -> generationResult.getOutput()
                    .getChoices().getFirst().getMessage().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GenerationParam buildGenerationParam(ChatModelConfigEntity config, String systemMessage, String userMessage, boolean stream) {
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(systemMessage).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(userMessage).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        return GenerationParam.builder()
                .apiKey(config.getApiKey())
                .model(config.getModel())
                .temperature(config.getTemperature())
                .messages(msgManager.get())
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(stream)
                .build();
    }
}
