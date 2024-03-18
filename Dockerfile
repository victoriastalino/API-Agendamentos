FROM openjdk:21-jdk
WORKDIR /app
COPY ./agendamentos/target/agendamentos-0.0.1-SNAPSHOT.jar /app/agendamentos.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "agendamentos.jar"]