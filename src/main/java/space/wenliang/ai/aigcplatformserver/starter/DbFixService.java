package space.wenliang.ai.aigcplatformserver.starter;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import space.wenliang.ai.aigcplatformserver.entity.RefAudioEntity;
import space.wenliang.ai.aigcplatformserver.service.application.ARefAudioService;

@Component
public class DbFixService implements ApplicationRunner {

    private final ARefAudioService aRefAudioService;

    public DbFixService(ARefAudioService aRefAudioService) {
        this.aRefAudioService = aRefAudioService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .set(RefAudioEntity::getLanguage, "zh")
                .eq(RefAudioEntity::getLanguage, "中文"));
        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .set(RefAudioEntity::getLanguage, "en")
                .eq(RefAudioEntity::getLanguage, "英文"));
        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .set(RefAudioEntity::getLanguage, "ja")
                .eq(RefAudioEntity::getLanguage, "日文"));
        aRefAudioService.update(new LambdaUpdateWrapper<RefAudioEntity>()
                .set(RefAudioEntity::getLanguage, "ko")
                .eq(RefAudioEntity::getLanguage, "韩文"));
    }
}
