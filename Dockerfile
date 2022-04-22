FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY /src/main/resources/data.csv data.csv
ENTRYPOINT ["java","-jar","/app.jar"]
