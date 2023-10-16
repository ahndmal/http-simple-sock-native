# build from Jar file

FROM maven:3.8.6-amazoncorretto-19 AS BUILD
COPY . .
RUN mvn clean package

# https://www.graalvm.org/22.1/docs/getting-started/container-images/
# https://github.com/graalvm/container/pkgs/container/jdk/versions?filters%5Bversion_type%5D=tagged
# FROM ghcr.io/graalvm/jdk:java17-22.1.0
FROM ghcr.io/graalvm/jdk:java
FROM findepi/graalvm:22.3.1-java19-native AS GRAAL
COPY --from=BUILD target/http-sock-3-0.0.2.jar /http-sock.jar
RUN gu install native-image \
      && echo ">> building from JAR file... \n" \
#      && apt install -y libc6 libc-bin \
      && cat /etc/os-release \
      && native-image -jar http-sock.jar

FROM debian:12-slim
#FROM debian:stable-slim
COPY --from=GRAAL /http-sock /tmp/http-sock
RUN apt install -y libc6 libc-bin
CMD ["/tmp/http-sock"]
