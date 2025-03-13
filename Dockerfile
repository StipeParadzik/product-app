FROM maven:3.9.1-eclipse-temurin-17 as builder

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

COPY . .

RUN mvn clean install -DskipTests

RUN java -Djarmode=layertools -jar app/target/app-*.jar extract


FROM amazoncorretto:17-alpine

WORKDIR /app
EXPOSE 8080

COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
