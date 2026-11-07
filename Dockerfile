FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY feedback-produtos/src/ ./feedback-produtos/src/

RUN mvn --batch-mode clean package -DskipTests

FROM tomcat:10.1.59-jdk17-temurin

COPY --from=build /app/target/feedback-produtos.war /usr/local/tomcat/webapps/feedback-produtos.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
