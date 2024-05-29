package space.wenliang.ai.aigcplatformserver.model.audio;

public interface IAudioCreater {

    byte[] createAudio(AudioContext context);

    void createFile(AudioContext context);

    void format(AudioContext context) throws Exception;
}
