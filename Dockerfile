FROM openjdk:17-jdk-alpine
COPY target/*.jar app.jar
EXPOSE 8080
LABEL authors="Guillermo"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/app.jar"]