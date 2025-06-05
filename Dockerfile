FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file (replace with your actual JAR name)
COPY target/*.jar app.jar

# Expose port (optional for local use)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
