# 使用 JRE 基础镜像
FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 复制并解析 pom.xml 获取版本号
COPY pom.xml /tmp/
RUN apt-get update && \
    apt-get install -y maven && \
    VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) && \
    echo "VERSION=${VERSION}" > /tmp/version.txt

# 复制构建好的 JAR 文件到工作目录
RUN VERSION=$(cat /tmp/version.txt | cut -d'=' -f2) && \
    cp target/aigc-platform-server-${VERSION}.jar /app/app.jar \

# 启动 Spring Boot 应用程序
CMD ["java", "--enable-preview", "-Dspring.profiles.active=prod,split,linux,mysql", "-jar", "/app/app.jar"]
