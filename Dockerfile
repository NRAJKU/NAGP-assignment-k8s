FROM eclipse-temurin:17-jre

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]