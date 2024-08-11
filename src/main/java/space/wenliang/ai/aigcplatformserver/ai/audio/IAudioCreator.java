package space.wenliang.ai.aigcplatformserver.ai.audio;

import org.springframework.http.ResponseEntity;

public interface IAudioCreator {

    void preCheck(AudioContext context);

    void pre(AudioContext context) throws Exception;

    ResponseEntity<byte[]> createAudio(AudioContext context);

    void createFile(AudioContext context);

    void post(AudioContext context) throws Exception;
}
