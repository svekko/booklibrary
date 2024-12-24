FROM openjdk:17-alpine
WORKDIR /usr/src/app
COPY . /usr/src/app
COPY ./src/main/resources/application.docker.properties /usr/src/app/src/main/resources/application.properties
EXPOSE 8080
CMD ["./gradlew", "bootRun"]
