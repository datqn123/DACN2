# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# 1. Copy pom.xml only first to cache dependencies
COPY pom.xml .
# 2. Download dependencies (Cached layer)
RUN mvn dependency:go-offline
# 3. Copy source code
COPY src ./src
# 4. Build (Fast - skips downloading if pom unchanged)
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]