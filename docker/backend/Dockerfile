FROM maven:3.9.11-eclipse-temurin-21

WORKDIR /app

COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline --batch-mode

COPY backend/src ./src

EXPOSE 8080

CMD ["mvn", "spring-boot:run", "--batch-mode"]

