FROM openjdk:8

COPY target/zipkin.jar /usr/local/bin
WORKDIR /usr/local/bin

CMD ["java", "-jar", "zipkin.jar"]

EXPOSE 9411