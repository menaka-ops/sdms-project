# Use an official Eclipse Temurin build image (Java 17)
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# ✅ --- THIS IS THE FIX ---
# Use the official Eclipse Temurin JRE slim image
FROM eclipse-temurin:17-jre-slim

# Set the working directory
WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/sdms-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port (Render will use this)
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]