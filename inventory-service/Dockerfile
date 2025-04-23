FROM maven:3.9.4-eclipse-temurin-21-alpine

WORKDIR /app

#COPY ./target/product-service-0.0.1-SNAPSHOT.jar .
#EXPOSE 8080
#CMD ["java", "-jar", "product-service-0.0.1-SNAPSHOT.jar"]

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]