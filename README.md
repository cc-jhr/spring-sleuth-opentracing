# spring-sleuth-opentracing 
![Travis build](https://travis-ci.org/MrBW/spring-sleuth-opentracing.svg?branch=master)<br>
Spring Sleuth &amp; OpenTracing demo

## Architecture
![Architecture](/docs/OpenTracingDemo.png)

## Endpoints
### ZipKin Server
http://localhost:9411

### NoteApp
- GET http://localhost:8090/hystrix/notes
- GET http://localhost:8090/notes
- GET http://localhost:8090/notes/{id}
- DELETE http://localhost:8090/notes/{id}
- POST http://localhost:8090/
with body:
```
{
    "noteMessage": "My Message"
}
```

### NoteBackend
- GET http://localhost:8080/notes
- GET http://localhost:8080/notes/{id}
- DELETE http://localhost:8080/notes/{id}
- POST http://localhost:8080/
with body:
```
{
    "noteMessage": "My Message"
}
```

## Start using docker-compose
* build all projects ```$ ./mvnw clean package```
* start up: ```$ docker-compose up --build```
