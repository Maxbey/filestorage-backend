FROM openjdk:8-jdk-alpine
ADD build/libs/filestorage.jar app.jar
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]