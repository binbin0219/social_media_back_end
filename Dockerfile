FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy source code
COPY . .

# Build the JAR
RUN ./mvnw clean package -DskipTests

# Copy the built JAR to app.jar
RUN cp target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
