package space.wenliang.ai.aigcplatformserver.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
@Profile("integrated")
public class IntegratedConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api";

    @Configuration
    public static class ApiPrefixFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String requestURI = httpRequest.getRequestURI();

            if (requestURI.startsWith(API_PREFIX)) {
                // 去掉 /api 前缀
                String newRequestURI = requestURI.substring(API_PREFIX.length());
                HttpServletRequestWrapper wrapper = new IntegratedRequestWrapper(httpRequest, newRequestURI);
                chain.doFilter(wrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    public static class IntegratedRequestWrapper extends HttpServletRequestWrapper {

        private final String newRequestURI;

        public IntegratedRequestWrapper(HttpServletRequest request, String newRequestURI) {
            super(request);
            this.newRequestURI = newRequestURI;
        }

        @Override
        public String getRequestURI() {
            return newRequestURI;
        }
    }
}
