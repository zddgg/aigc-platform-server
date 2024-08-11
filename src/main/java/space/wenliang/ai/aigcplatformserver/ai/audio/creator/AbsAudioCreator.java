package space.wenliang.ai.aigcplatformserver.ai.audio.creator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioContext;
import space.wenliang.ai.aigcplatformserver.ai.audio.IAudioCreator;
import space.wenliang.ai.aigcplatformserver.bean.PolyphonicInfo;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.cache.PinyinCacheService;
import space.wenliang.ai.aigcplatformserver.util.AudioUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class AbsAudioCreator implements IAudioCreator {

    public final RestClient restClient;
    public final PinyinCacheService pinyinCacheService;

    protected AbsAudioCreator(RestClient restClient,
                              PinyinCacheService pinyinCacheService) {
        this.restClient = restClient;
        this.pinyinCacheService = pinyinCacheService;
    }

    public void textMarkup(AudioContext context) {
        StringBuilder markupText = new StringBuilder(context.getText());
        if (Objects.nonNull(context.getTextMarkupInfo())) {
            List<PolyphonicInfo> polyphonicInfos = context.getTextMarkupInfo().getPolyphonicInfos();
            for (PolyphonicInfo polyphonicInfo : polyphonicInfos) {
                if (Objects.nonNull(polyphonicInfo.getIndex())
                        && Objects.nonNull(polyphonicInfo.getMarkup())
                        && polyphonicInfo.getIndex() >= context.getTextPartIndexStart()
                        && polyphonicInfo.getIndex() <= context.getTextPartIndexEnd()) {
                    String uniHan = pinyinCacheService.getUniHanByPinyin(polyphonicInfo.getMarkup());
                    if (Objects.nonNull(uniHan)) {
                        markupText.setCharAt(polyphonicInfo.getIndex() - context.getTextPartIndexStart(), uniHan.charAt(0));
                    }
                }
            }

        }
        context.setMarkupText(markupText.toString());
    }

    public Map<String, Object> buildParams(AudioContext context) {
        return Map.of();
    }

    @Override
    public void preCheck(AudioContext context) {
        if (Objects.isNull(context.getAmServer())) {
            throw new BizException("[" + context.getAmType() + "]类型的api服务没有配置");
        }
    }

    @Override
    public void pre(AudioContext context) {

    }

    public ResponseEntity<byte[]> createAudio(AudioContext context) {

        preCheck(context);

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
            throw new BizException("音频生成服务连接异常，服务类型：" + context.getAmType());
        } catch (Exception e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException(e.getMessage());
        }
        return null;
    }

    public void createFile(AudioContext context) {

        preCheck(context);

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
                byte[] bytes = AudioUtils.audioFormat(response.getBody());
                Files.write(path, bytes);
                log.info("write file, context: {}", context);
            }

        } catch (ResourceAccessException e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException("音频生成服务连接异常，服务类型：" + context.getAmType());
        } catch (Exception e) {
            log.error("write exception, context: {}", context, e);
            throw new BizException(e.getMessage());
        }

    }

    @Override
    public void post(AudioContext context) {

    }

    public ResponseEntity<byte[]> creator(AudioContext context) {

        textMarkup(context);

        Map<String, Object> params = buildParams(context);

        return restClient
                .post()
                .uri(context.getAmServer().getHost() + context.getAmServer().getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .toEntity(byte[].class);
    }
}
