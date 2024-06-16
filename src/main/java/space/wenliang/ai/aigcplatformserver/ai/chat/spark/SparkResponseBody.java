package space.wenliang.ai.aigcplatformserver.ai.chat.spark;

import lombok.Data;

import java.util.List;

@Data
public class SparkResponseBody {
    private Header header;
    private Payload payload;


    @Data
    public static class Header {
        private int code;
        private String message;
        private String sid;
        private int status;
    }

    @Data
    public static class Payload {
        private Choices choices;
        private Usage usage;

        @Data
        public static class Choices {
            private int status;
            private int seq;
            private List<Text> text;

            @Data
            public static class Text {
                private String content;
                private String role;
                private int index;

            }
        }

        @Data
        public static class Usage {
            private Text text;

            @Data
            public static class Text {
                private int question_tokens;
                private int prompt_tokens;
                private int completion_tokens;
                private int total_tokens;
            }
        }
    }
}
