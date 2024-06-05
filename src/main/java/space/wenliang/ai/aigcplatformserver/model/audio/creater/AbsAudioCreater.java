package space.wenliang.ai.aigcplatformserver.model.audio.creater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.model.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.model.audio.IAudioCreater;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class AbsAudioCreater implements IAudioCreater {

    public RestClient restClient;

    protected AbsAudioCreater(RestClient restClient) {
        this.restClient = restClient;
    }

    public abstract Map<String, Object> buildParams(AudioContext context);

    @Override
    public void pre(AudioContext context) {

    }

    public byte[] createAudio(AudioContext context) {
        pre(context);

        Map<String, Object> params = buildParams(context);

        ResponseEntity<byte[]> response = restClient.post()
                .uri(context.getAudioServerConfig().getServerUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .toEntity(byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
            try {
                return response.getBody();
            } catch (Exception e) {
                log.error("write exception, context: {}", context, e);
                throw new BizException("create audio failed");
            }
        }

        return null;
    }

    public void createFile(AudioContext context) {
        pre(context);

        Map<String, Object> params = buildParams(context);

        ResponseEntity<byte[]> response = restClient.post()
                .uri(context.getAudioServerConfig().getServerUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .toEntity(byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
            try {
                Path path = Path.of(context.getOutputDir(), context.getOutputName() + "." + context.getMediaType());
                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, response.getBody());
                post(context);
                log.info("write file, context: {}", context);
            } catch (Exception e) {
                log.error("write exception, context: {}", context, e);
                throw new BizException("create audio failed");
            }
        } else {
            log.error("write exception, context: {}, response: {}", context, response);
            throw new BizException("create audio failed");
        }

    }

    @Override
    public void post(AudioContext context) throws Exception {

    }
}
