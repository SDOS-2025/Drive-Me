FROM openjdk:21-slim


WORKDIR /app
COPY build/libs/driveme-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy entrypoint
EXPOSE 3306 8080

RUN chmod +x /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

