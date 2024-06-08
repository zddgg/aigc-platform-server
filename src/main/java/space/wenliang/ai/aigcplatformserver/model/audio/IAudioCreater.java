package space.wenliang.ai.aigcplatformserver.model.audio;

import org.springframework.http.ResponseEntity;

public interface IAudioCreater {

    void pre(AudioContext context) throws Exception;

    ResponseEntity<byte[]> createAudio(AudioContext context);

    void createFile(AudioContext context);

    void post(AudioContext context) throws Exception;
}
