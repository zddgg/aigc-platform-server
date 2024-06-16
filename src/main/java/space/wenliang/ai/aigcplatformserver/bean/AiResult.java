package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiResult {

    private List<Role> roles = new ArrayList<>();

    private List<LinesMapping> linesMappings = new ArrayList<>();

    @Data
    public static class Role {
        private String role;
        private String gender;
        private String ageGroup;

        public Role() {
        }

        public Role(String role) {
            this.role = role;
            this.gender = "未知";
            this.ageGroup = "未知";
        }
    }

    @Data
    public static class LinesMapping {
        private String linesIndex;
        private String role;
        private String gender;
        private String ageGroup;
        private String mood;
    }
}
