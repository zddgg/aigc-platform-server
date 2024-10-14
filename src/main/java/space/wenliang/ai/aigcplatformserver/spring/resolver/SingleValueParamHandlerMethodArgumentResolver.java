package space.wenliang.ai.aigcplatformserver.spring.resolver;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;

import java.io.BufferedReader;
import java.util.Objects;

public class SingleValueParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REQUEST_BODY_ATTRIBUTE = "REQUEST_BODY_ATTRIBUTE";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SingleValueParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        SingleValueParam singleValueParam = parameter.getParameterAnnotation(SingleValueParam.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (Objects.isNull(request) || Objects.isNull(singleValueParam)) {
            return null;
        }

        JSONObject jsonObject = (JSONObject) request.getAttribute(REQUEST_BODY_ATTRIBUTE);
        if (jsonObject == null) {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int rd;
            while ((rd = reader.read(buf)) != -1) {
                sb.append(buf, 0, rd);
            }
            jsonObject = JSONObject.parseObject(sb.toString());
            request.setAttribute(REQUEST_BODY_ATTRIBUTE, jsonObject);
        }

        String value = singleValueParam.value();
        if (Objects.isNull(jsonObject)) {
            return null;
        }
        return jsonObject.get(value);
    }
}
