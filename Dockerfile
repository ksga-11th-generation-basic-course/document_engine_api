FROM openjdk:20-jdk

#dir for docker img
WORKDIR /app

#dir local
COPY target/DocEngine-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD java -jar DocEngine-0.0.1-SNAPSHOT.jar
