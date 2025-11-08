# Use Eclipse Temurin OpenJDK 11 runtime as base image
FROM eclipse-temurin:11-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY infinity/mvnw .
COPY infinity/.mvn .mvn
COPY infinity/pom.xml .

# Copy source code
COPY infinity/src src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/infinity-0.0.1-SNAPSHOT.jar"]
