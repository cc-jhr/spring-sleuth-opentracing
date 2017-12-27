FROM openjdk:8

COPY target/note.jar /usr/local/bin
WORKDIR /usr/local/bin

CMD ["java", "-jar", "note.jar", "--note.base.url=http://note-backend:8080", "--spring.zipkin.baseUrl=http://zipkin:9411"]

EXPOSE 8090