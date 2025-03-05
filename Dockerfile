FROM openjdk:21-jdk-slim

ADD target/recipes-service-1.0.jar recipes-service.jar

EXPOSE 8100

ENTRYPOINT ["java", "-jar", "recipes-service.jar"]
