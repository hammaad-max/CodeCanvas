# CodeCanvas container image (used by managed platforms such as Render).
# Build:  docker build -t codecanvas .
# Run:    docker run -p 8080:8080 -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... codecanvas

# ---- Build stage: compile, run the test suite (in-memory H2) and package the jar ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /build
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sh mvnw -B -q dependency:go-offline
COPY src/ src/
RUN sh mvnw -B verify && cp target/CodeCanvas-0.0.1-SNAPSHOT.jar /build/app.jar

# ---- Runtime stage: JRE only, non-root user ----
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 --no-create-home codecanvas
WORKDIR /app
COPY --from=build /build/app.jar app.jar
USER codecanvas
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
