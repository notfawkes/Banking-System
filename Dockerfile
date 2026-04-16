# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the POM first and download dependencies to utilize Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source tree and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the constructed JAR from the build stage
COPY --from=build /app/target/app.jar ./app.jar

# Expose the Render default port (8080)
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
