# --- Stage 1: Build Stage ---
# We use a full JDK image to compile the code
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy the maven wrapper and pom file first 
# (This allows Docker to cache your dependencies so builds are faster)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copy the source code and build the JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Stage 2: Runtime Stage ---
# We switch to a smaller JRE image (Runtime only, no compiler) 
# This makes the final image much smaller and more secure.
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy only the final JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Open port 8080 to the world
EXPOSE 8080

# The command to run your app
ENTRYPOINT ["java", "-jar", "app.jar"]
