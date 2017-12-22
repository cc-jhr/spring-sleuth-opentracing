package de.codecentric.opentracing.instana.demo.notebackend.rest;

import de.codecentric.opentracing.instana.demo.notebackend.dto.Note;
import de.codecentric.opentracing.instana.demo.notebackend.persistence.NoteEntity;
import de.codecentric.opentracing.instana.demo.notebackend.persistence.NoteRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Benjamin Wilms
 */
@RestController
public class NoteBackendController {
    private static final Logger log = LoggerFactory.getLogger(NoteBackendController.class);

    private final NoteRepo noteRepo;
    private final Tracer tracer;

    public NoteBackendController(NoteRepo noteRepo, Tracer tracer) {
        this.noteRepo = noteRepo;
        this.tracer = tracer;
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable(value = "id") Long id) {
        NoteEntity noteEntity = noteRepo.findOne(id);

        if (noteEntity == null) {

            // OpenTracing / Sleuth
            logEvent("note not found by id: " + id);

            return ResponseEntity.notFound().build();
        }
        logEvent("note found by id: " + id);
        tagSpan(noteEntity);
        return ResponseEntity.ok().body(new Note(noteEntity.getId(), noteEntity.getNote()));

    }

    @GetMapping("/notes")
    public ResponseEntity<List<Note>> getAllNotes() {

        List<Note> noteList;
        try {
            noteList = noteRepo.findAll().stream().map(noteEntity -> new Note(noteEntity.getId(), noteEntity.getNote())).collect(Collectors.toList());

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
        NoteEntity noteEntity;
        try {
            noteEntity = noteRepo.save(new NoteEntity(note.getNoteMessage()));

            // OpenTracing / Sleuth
            logEvent("added: " + noteEntity.getId());
            tagSpan(noteEntity);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new Note(noteEntity.getId(), noteEntity.getNote()));
    }

    private void tagSpan(NoteEntity noteEntity) {
        tagSpan("id", noteEntity.getId().toString());
        tagSpan("note", StringUtils.substring(noteEntity.getNote(), 0, 10));
    }

    @DeleteMapping("notes/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable(value = "id") Long id) {
        try {
            noteRepo.delete(id);

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

        currentSpan.tag("demo.backend." + key, content);

    }

    private Span getCurrentSpan() {
        Span span = tracer.getCurrentSpan();
        return tracer.continueSpan(span);
    }
}
