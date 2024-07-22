FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=dev","app.jar"]
