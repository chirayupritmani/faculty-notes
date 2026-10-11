# Faculty Notes Publishing System - container image
# The WAR is built first with Maven (mvn package); this image only runs it.
FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.title="faculty-notes" \
      org.opencontainers.image.description="Faculty Notes Publishing System (Spring Boot WAR)"

WORKDIR /app

# Run as a normal user, not root
RUN useradd --system --no-create-home appuser

COPY target/faculty-notes.war /app/app.war

# The application reads its port from the PORT variable
ENV PORT=8080
EXPOSE 8080

USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.war"]
