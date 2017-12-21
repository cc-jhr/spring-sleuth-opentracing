package de.codecentric.opentracing.instana.demo.note.rest;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import de.codecentric.opentracing.instana.demo.note.dto.Note;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Benjamin Wilms
 */
@RestController
public class NoteController {
    private static final Logger log = LoggerFactory.getLogger(NoteController.class);

    private final RestTemplate restTemplate;
    private final Tracer tracer;

    private String baseUrl;

    public NoteController(RestTemplate restTemplate, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.tracer = tracer;
        this.baseUrl = "http://localhost:8080/";
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable(value = "id") Long id) {

        Note note = null;
        try {
            note = restTemplate.getForObject(baseUrl + "/" + id, Note.class);
            tagSpan(note);
            return ResponseEntity.ok().body(note);

        } catch (RestClientException e) {
            // OpenTracing / Sleuth
            logEvent("note not found by id: " + id);

            return ResponseEntity.notFound().build();
        }


    }

    @GetMapping("/hystrix/list")
    @HystrixCommand(fallbackMethod = "fallbackListAll")
    public ResponseEntity<List<Note>> getAllNotesCoveredByHystrix() {
        if(RandomUtils.nextBoolean()){

            return getAllNotes();
        } else {
            logEvent("call Hystrix fallback method");
            throw new RuntimeException("Random error");
        }

    }

    public ResponseEntity<String> fallbackListAll() {
        logEvent("Hystrix fallback method called");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Hystrix Fallback");
    }

    @GetMapping("/notes")
    public ResponseEntity<List<Note>> getAllNotes() {

        List<Note> noteList;
        try {

            ResponseEntity<List<Note>> noteResponse =
                    restTemplate.exchange(
                        baseUrl + "/notes",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Note>>() {
                        }
                    );

            noteList = noteResponse.getBody();

            // OpenTracing / Sleuth
            logEvent("get all notes, count: " + noteList.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(noteList);
    }

    @PostMapping
    public ResponseEntity<Note> postNote(@RequestBody Note note) {
        Note noteReponse;
        try {

            noteReponse = restTemplate.patchForObject(baseUrl, note, Note.class);

            // OpenTracing / Sleuth
            logEvent("added: " + noteReponse.getId());
            tagSpan(noteReponse);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(noteReponse);
    }

    private void tagSpan(Note note) {
        tagSpan("id", note.getId().toString());
        tagSpan("note", note.getNoteMessage());
    }

    @DeleteMapping("notes/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable(value = "id") Long id) {
        try {

            restTemplate.delete(baseUrl + "/id");

            // OpenTracing / Sleuth
            logEvent("deleted: " + id);
            tagSpan("id", id.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("deleted " + id);

    }

    private void logEvent(String event) {
        Span currentSpan = getCurrentSpan();
        currentSpan.logEvent(event);
    }

    private void tagSpan(String key, String content) {
        Span currentSpan = getCurrentSpan();

        currentSpan.tag("demo.client." + key, content);

    }

    private Span getCurrentSpan() {
        Span span = tracer.getCurrentSpan();
        return tracer.continueSpan(span);
    }


}
