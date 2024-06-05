package space.wenliang.ai.aigcplatformserver.model.chat.openAi;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponseBody {

    private String id;
    private Long created;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Integer index;
        private String finish_reason;
        private Delta delta;
    }

    @Data
    public static class Delta {
        private String role;
        private String content;
    }
}
