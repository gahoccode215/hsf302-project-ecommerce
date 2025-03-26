# Stage 1: Build ứng dụng Spring Boot
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình Maven và source code vào container
COPY pom.xml .
COPY src ./src

# Build ứng dụng Spring Boot bằng Maven
RUN mvn clean package -DskipTests

# Stage 2: Tạo image chạy ứng dụng
FROM amazoncorretto:21.0.4

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]