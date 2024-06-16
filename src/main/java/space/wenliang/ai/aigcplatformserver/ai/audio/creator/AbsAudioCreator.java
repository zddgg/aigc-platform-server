package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.IAudioCreator;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.util.AudioUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class AbsAudioCreator implements IAudioCreator {

    public RestClient restClient;

    protected AbsAudioCreator(RestClient restClient) {
        this.restClient = restClient;
    }

    public abstract Map<String, Object> buildParams(AudioContext context);

    @Override
    public void pre(AudioContext context) {

    }

    public ResponseEntity<byte[]> createAudio(AudioContext context) {

        pre(context);

        try {
            log.info("Creating audio file...");
            ResponseEntity<byte[]> response = creator(context);
            log.info("Creating audio file done.");

            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
                return response;
            }
        } catch (ResourceAccessException e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException("音频生成服务连接异常，服务类型：" + context.getType());
        } catch (Exception e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException(e.getMessage());
        }
        return null;
    }

    public void createFile(AudioContext context) {
        pre(context);

        try {
            ResponseEntity<byte[]> response = creator(context);

            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {

                Path path = Path.of(context.getOutputDir(), context.getOutputName() + "." + context.getMediaType());
                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                AudioUtils.wavFormat(response.getBody(), path.toAbsolutePath().toString());
                log.info("write file, context: {}", context);
            }

        } catch (ResourceAccessException e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException("音频生成服务连接异常，服务类型：" + context.getType());
        } catch (Exception e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException(e.getMessage());
        }

    }

    @Override
    public void post(AudioContext context) {

    }

    private ResponseEntity<byte[]> creator(AudioContext context) {

        Map<String, Object> params = buildParams(context);

        return restClient
                .post()
                .uri(context.getAudioServerConfig().getHost() + context.getAudioServerConfig().getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .toEntity(byte[].class);
    }
}
