FROM openjdk:8
COPY build/libs/dock-doors-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java","-jar","/app.jar"]

