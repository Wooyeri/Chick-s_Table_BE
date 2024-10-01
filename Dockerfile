FROM openjdk:17
COPY build/libs/chickstable.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]