# 使用 JRE 基础镜像
FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 文件到工作目录
COPY target/aigc-platform-server-1.0.0.jar /app/app.jar

# 启动 Spring Boot 应用程序
CMD ["java", "--enable-preview", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
