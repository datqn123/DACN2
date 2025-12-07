# 1. Build giai đoạn đầu (Dùng Maven để đóng gói)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. Giai đoạn chạy (Chỉ lấy file jar để chạy cho nhẹ)
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/dacn2-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]