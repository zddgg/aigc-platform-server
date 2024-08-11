package space.wenliang.ai.aigcplatformserver.ai.audio;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.bean.TextMarkupInfo;
import space.wenliang.ai.aigcplatformserver.entity.AmServerEntity;
import space.wenliang.ai.aigcplatformserver.entity.AudioModelInfo;

@Data
public class AudioContext extends AudioModelInfo {

    private String text;
    private String textLang;
    private TextMarkupInfo textMarkupInfo;

    private Integer textPartIndexStart;
    private Integer textPartIndexEnd;
    private String markupText;

    private String mediaType = "wav";
    private String outputDir;
    private String outputName;

    private AmServerEntity amServer;
}
