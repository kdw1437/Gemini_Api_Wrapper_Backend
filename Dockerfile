# --- Stage 1: Build the Application ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy all project files
COPY . .

# ⚠️ CRITICAL FOR WINDOWS USERS: Fix line endings
# This converts Windows text format to Linux format so the script runs
RUN sed -i 's/\r$//' gradlew

# Make the gradle wrapper executable
RUN chmod +x gradlew

# Build the jar (skipping tests to save time)
RUN ./gradlew bootJar --no-daemon -x test

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the build folder (Gradle puts it in build/libs)
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run with memory limits for Render Free Tier
ENTRYPOINT ["java", "-Xmx350m", "-Xms350m", "-jar", "app.jar"]