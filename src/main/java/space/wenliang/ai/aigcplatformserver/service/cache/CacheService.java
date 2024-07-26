package space.wenliang.ai.aigcplatformserver.service.cache;

import java.util.List;
import java.util.Map;

public interface CacheService {

    Map<String, List<String>> getTextPinyins();

    Map<String, List<String>> getPinyinTexts();
}
