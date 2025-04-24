FROM openjdk:24-jdk-alpine as build
COPY mvnw /code/mvnw
COPY .mvn /code/.mvn
COPY pom.xml /code/

WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY src /code/src
RUN ./mvnw package

## Stage 2 : create the docker final image
FROM openjdk:24-jdk-alpine
WORKDIR /work/application
COPY --from=build /code/target/bookstore-backend*.jar /work/application/bookstore-backend.jar

EXPOSE 8080

CMD ["java", "-jar", "bookstore-backend.jar"]