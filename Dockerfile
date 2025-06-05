FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy everything (including mvnw, pom.xml, src/)
COPY . .

# Give execute permission to the Maven wrapper script
RUN chmod +x mvnw

# Build the JAR file
RUN ./mvnw clean package -DskipTests

# Rename the built JAR to app.jar
RUN cp target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
