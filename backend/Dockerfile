# Use a Java image as a base
FROM maven:3.8.7-eclipse-temurin-17-alpine

# Create a directory for the app
WORKDIR /app

#  Copy the compiled JAR (first copy the POM for better caching)
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Copy only the resulting JAR
COPY target/*.jar app.jar

# Expose the port
EXPOSE ${SERVER_PORT}

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]