package space.wenliang.ai.aigcplatformserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import space.wenliang.ai.aigcplatformserver.spring.resolver.SingleValueParamHandlerMethodArgumentResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/webui/**")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SingleValueParamHandlerMethodArgumentResolver());
        WebMvcConfigurer.super.addArgumentResolvers(argumentResolvers);
    }


    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);

    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(600000);//单位为ms
        factory.setConnectTimeout(30000);//单位为ms
        return factory;
    }

    @Bean
    public RestClient restClient(ClientHttpRequestFactory factory) {
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient msgpackWebClient() {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

        return WebClient.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(
                            new Jackson2JsonEncoder(objectMapper, MediaType.parseMediaType("application/msgpack"))
                    );
                    configurer.defaultCodecs().jackson2JsonDecoder(
                            new Jackson2JsonDecoder(objectMapper, MediaType.parseMediaType("application/msgpack"))
                    );
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024);  // 设置最大缓冲区为 10MB
                })
                .build();

    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}