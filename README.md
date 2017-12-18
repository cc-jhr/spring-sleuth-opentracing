# spring-sleuth-opentracing
Spring Sleuth &amp; OpenTracing demo

## Architecture
![Architecture](/docs/OpenTracingDemo.png)

## Endpoints
### ZipKin Server
http://localhost:9411

### NoteApp
GET http://localhost:8090/list
GET http://localhost:8090/note/{id}
DELETE http://localhost:8090/note/{id}
POST http://localhost:8090/

### NoteBackend
GET http://localhost:8080/list
GET http://localhost:8080/note/{id}
DELETE http://localhost:8080/note/{id}
POST http://localhost:8080/

