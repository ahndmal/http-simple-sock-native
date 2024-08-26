# build from Jar file
# https://www.graalvm.org/22.1/docs/getting-started/container-images/
# https://github.com/graalvm/container/pkgs/container/jdk/versions?filters%5Bversion_type%5D=tagged

FROM maven:3-eclipse-temurin-22-alpine AS build
COPY . .
RUN mvn clean package

# FROM ghcr.io/graalvm/jdk:java17-22.1.0
#FROM ghcr.io/graalvm/jdk:java as graal
FROM findepi/graalvm:java22-native AS graal
COPY --from=build target/http-sock-3-*.jar /http-sock.jar
#RUN gu install native-image \
#      && apt install -y libc6 libc-bin \
#      && cat /etc/os-release \
RUN native-image -jar http-sock.jar

FROM debian:12-slim
#FROM debian:stable-slim
COPY --from=graal /http-sock /tmp/http-sock
RUN apt install -y libc6 libc-bin
CMD ["/tmp/http-sock"]
