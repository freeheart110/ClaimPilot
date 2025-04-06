# Use official Java 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy everything into container
COPY . .

# Build your Spring Boot app (skip tests if needed)
RUN ./mvnw clean package -DskipTests

# Run the Spring Boot app
CMD ["java", "-jar", "target/claimpilot-0.0.1-SNAPSHOT.jar"]