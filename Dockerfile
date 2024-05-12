# Use the official Maven image as base image for building the JavaFX application
FROM maven:3.8.3-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the source code into the container
COPY . /app

# Build the JavaFX application JAR file
RUN mvn clean package -e

# Use the official OpenJDK image as base image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the builder stage into the container
COPY --from=builder /app/target/demo-1.0-SNAPSHOT.jar /app

# Expose the port the application runs on
EXPOSE 8080

# Command to run the JavaFX application
CMD ["java", "-jar", "/app/demo-1.0-SNAPSHOT.jar"]
