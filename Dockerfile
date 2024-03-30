#Dockerfile 코드

FROM openjdk:17-alpine
ARG JAR_FILE=./build/libs/ahchacha-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -jar /app.jar"]