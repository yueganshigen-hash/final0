# 第一步：构建 jar
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

# 第二步：运行 jar
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
