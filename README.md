# http-simple-sock-native
Simple HTTP service compiled to Jar or Native image for Cloud services testing.


```bash
docker build --tag java-http-simple:0.0.1 .
```

```bash
# Jar
mvn clean package

# native
mvn clean package -Pnative
```

