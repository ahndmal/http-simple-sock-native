# build from Jar file

FROM maven:3.8.6-amazoncorretto-19 AS BUILD
COPY . .
RUN mvn clean package

# https://www.graalvm.org/22.1/docs/getting-started/container-images/
# ghcr.io/graalvm/jdk:java17-22.1.0
FROM findepi/graalvm:22.3.1-java19-native AS GRAAL
COPY --from=BUILD target/http-sock-3-0.0.2.jar /http-sock.jar
RUN gu install native-image \
      && echo ">> building from JAR file...\n" \
      && native-image -jar http-sock.jar

FROM debian:bullseye-slim
COPY --from=GRAAL /http-sock /tmp/http-sock
CMD ["/tmp/http-sock"]
