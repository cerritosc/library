FROM openjdk:17-oracle

WORKDIR /app
COPY ./target/library-1.0.jar /app

EXPOSE 8080

CMD ["java", "-jar", "library-1.0.jar"]