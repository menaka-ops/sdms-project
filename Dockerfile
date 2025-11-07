# Use an official Amazon Corretto build image (Java 17)
FROM maven:3-amazoncorretto-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# Use the official Amazon Corretto JRE (Alpine is very small)
FROM amazoncorretto:17-alpine-jdk

# Set the working directory
WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/sdms-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port Render expects
EXPOSE 10000

# ✅ --- THIS IS THE FIX ---
# Run the app and tell Spring to use the $PORT variable
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]