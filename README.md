# http-simple-sock-native
Simple HTTP service compiled to Jar or Native image for Cloud services testing.

- https://medium.com/@quadr988/java-cloud-service-with-15mb-ram-9f30e52ca2f1
- https://dev.to/andriimaliuta/gcp-serverless-function-in-java-with-15mb-ram-ace

```bash
docker build --tag java-http-simple:0.0.1 .
```

```bash
# Jar
mvn clean package

# native
mvn clean package -Pnative
```

