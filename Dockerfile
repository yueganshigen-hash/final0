FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 只复制 backend
COPY backend/ .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
