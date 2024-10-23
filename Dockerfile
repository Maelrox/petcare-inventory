FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY build/libs/appointment-0.1.jar appointment.jar

ENTRYPOINT ["java", "-jar", "appointment.jar"]