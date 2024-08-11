package space.wenliang.ai.aigcplatformserver.ai.audio;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.AmServerEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.AmServerService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AudioCreator {

    private final AmServerService amServerService;
    private final Map<String, IAudioCreator> audioCreatorMap;

    public ResponseEntity<byte[]> createAudio(AudioContext context) {
        if (audioCreatorMap.containsKey(context.getAmType())) {
            addAudioServer(context);
            return audioCreatorMap.get(context.getAmType()).createAudio(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getAmType());
        }
    }

    public void createFile(AudioContext context) {
        if (audioCreatorMap.containsKey(context.getAmType())) {
            addAudioServer(context);
            audioCreatorMap.get(context.getAmType()).createFile(context);
        } else {
            throw new BizException("audio creater not exist, type: " + context.getAmType());
        }
    }

    private void addAudioServer(AudioContext context) {
        AmServerEntity amServer = amServerService.getOne(new LambdaQueryWrapper<AmServerEntity>()
                .eq(AmServerEntity::getName, context.getAmType()));
        context.setAmServer(amServer);
    }
}
