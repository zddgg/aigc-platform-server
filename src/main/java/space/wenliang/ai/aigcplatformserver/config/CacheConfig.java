package space.wenliang.ai.aigcplatformserver.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.wenliang.ai.aigcplatformserver.common.CacheEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> list = new ArrayList<>();
        for (CacheEnum cacheEnum : CacheEnum.values()) {
            list.add(new CaffeineCache(cacheEnum.getName(),
                    Caffeine.newBuilder()
                            .initialCapacity(0)
                            .maximumSize(1000)
                            .expireAfterAccess(cacheEnum.getExpires(), TimeUnit.SECONDS)
                            .build()));
        }
        cacheManager.setCaches(list);
        return cacheManager;
    }
}