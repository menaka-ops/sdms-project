# Use an official Maven image to build the app
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# Use a slim Java 17 image for the final, smaller application
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/sdms-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port (Render will use this)
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]