# Build stage using Maven + JDK
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the project
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage using slim OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/kaldar-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8050

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]