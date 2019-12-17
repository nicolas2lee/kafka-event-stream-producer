FROM maven:3.6-jdk-8-alpine AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B package


FROM openjdk:8-jre-alpine
COPY --from=builder /app/target/kafka-akka-avro-producer.jar /app.jar
ENTRYPOINT ["sh", "-c", "java -Dconfig.file=/conf/application.conf -Dlogback.configurationFile=/logback/logback.xml -jar /app.jar"]
