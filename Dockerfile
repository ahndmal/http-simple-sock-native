# build from Jar file

FROM maven:3.8.6-amazoncorretto-19 AS BUILD
COPY . .
RUN mvn clean package

FROM findepi/graalvm:22.3.1-java19-native
COPY --from=BUILD target/http-sock-3-0.0.2.jar /http-sock.jar
RUN gu install native-image && echo ">> building from JAR file...\n" && native-image -jar http-sock.jar
CMD ["my-app"]
