FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY target/backend.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080