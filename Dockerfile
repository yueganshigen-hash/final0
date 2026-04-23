FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY . .   # ✅ 直接复制当前项目

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
