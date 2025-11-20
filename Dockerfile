# 1) Use Java 21 base image
FROM eclipse-temurin:21-jdk

# 2) Create working directory inside container
WORKDIR /app

# 3) Copy jar file from Maven build into container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 4) Run the jar when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

