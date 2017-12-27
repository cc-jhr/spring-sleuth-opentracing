FROM openjdk:8

COPY target/note-backend.jar /usr/local/bin
WORKDIR /usr/local/bin

CMD ["java", "-jar", "note-backend.jar", "--spring.zipkin.baseUrl=http://zipkin:9411"]

EXPOSE 8080