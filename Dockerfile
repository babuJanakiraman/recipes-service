FROM openjdk:21-jdk-slim

ADD target/recipe-service-1.0.jar recipe-service.jar

EXPOSE 8100

ENTRYPOINT ["java", "-jar", "employee-database.jar"]