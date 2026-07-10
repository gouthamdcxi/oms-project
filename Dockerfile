# ---- Stage 1: Build the jar using Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first and download dependencies separately.
# Docker caches this layer, so dependencies aren't re-downloaded
# every time you change your Java code - only when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Run the jar with a lightweight JRE ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the built jar from the "build" stage above
# (none of the Maven cache or source code comes along)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
