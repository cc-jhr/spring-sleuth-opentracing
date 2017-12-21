# spring-sleuth-opentracing
Spring Sleuth &amp; OpenTracing demo

## Architecture
![Architecture](/docs/OpenTracingDemo.png)

## Endpoints
### ZipKin Server
http://localhost:9411

### NoteApp
- GET http://localhost:8090/hystrix/list
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