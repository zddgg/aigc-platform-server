package space.wenliang.ai.aigcplatformserver.model.audio;

public interface IAudioCreater {

    void pre(AudioContext context) throws Exception;

    byte[] createAudio(AudioContext context);

    void createFile(AudioContext context);

    void post(AudioContext context) throws Exception;
}
