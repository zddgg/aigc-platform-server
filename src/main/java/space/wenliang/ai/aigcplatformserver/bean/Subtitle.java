package space.wenliang.ai.aigcplatformserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subtitle {
    private double startTime;
    private double endTime;
    private String text;
}
