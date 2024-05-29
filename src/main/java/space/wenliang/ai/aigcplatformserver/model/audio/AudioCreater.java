package space.wenliang.ai.aigcplatformserver.model.audio;

import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.exception.BizException;

import java.util.Map;

@Service
public class AudioCreater {

    private final Map<String, IAudioCreater> audioCreaterMap;

    public AudioCreater(Map<String, IAudioCreater> audioCreaterMap) {
        this.audioCreaterMap = audioCreaterMap;
    }

    public byte[] createAudio(AudioContext context) {
        if (audioCreaterMap.containsKey(context.getType())) {
            return audioCreaterMap.get(context.getType()).createAudio(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }

    public void createFile(AudioContext context) {
        if (audioCreaterMap.containsKey(context.getType())) {
            audioCreaterMap.get(context.getType()).createFile(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getType());
        }
    }
}
