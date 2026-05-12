FROM eclipse-temurin:25-jdk AS builder

WORKDIR /build

COPY .mvn/ .mvn

COPY pom.xml mvnw ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]