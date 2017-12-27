package reminder.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reminder.dto.Remind;
import reminder.persistence.RemindEntity;
import reminder.persistence.ReminderRepo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Benjamin Wilms
 */
@RestController
public class ReminderController {
    private static final Logger log = LoggerFactory.getLogger(ReminderController.class);

    private final ReminderRepo reminderRepo;
    private final Tracer tracer;

    public ReminderController(ReminderRepo noteRepo, Tracer tracer) {
        this.reminderRepo = noteRepo;
        this.tracer = tracer;
    }

    @GetMapping("/reminds/{id}")
    public ResponseEntity<Remind> getNoteById(@PathVariable(value = "id") Long id) {
        RemindEntity remindEntity = reminderRepo.findOne(id);

        if (remindEntity == null) {

            // OpenTracing / Sleuth
            logEvent("remind not found by id: " + id);

            return ResponseEntity.notFound().build();
        }
        logEvent("remind found by id: " + id);
        tagSpan(remindEntity);
        return ResponseEntity.ok().body(new Remind(remindEntity.getNoteReferenceId(), remindEntity.getRemindDateTime()));

    }

    @GetMapping("/reminds")
    public ResponseEntity<List<Remind>> getAllNotes() {

        List<Remind> remindList;
        try {
            remindList = reminderRepo.findAll().
                    stream()
                    .map(remindEntity -> new Remind(remindEntity.getNoteReferenceId(), remindEntity.getRemindDateTime()))
                    .collect(Collectors.toList());

            // OpenTracing / Sleuth
            logEvent("get all reminds, count: " + remindList.size());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(remindList);
    }

    @PostMapping
    public ResponseEntity<Remind> postNote(@RequestBody Remind remind) {
        RemindEntity remindEntity;
        try {
            remindEntity = reminderRepo.save(new RemindEntity(remind.getNoteReferenceId(), remind.getRemindDateTime()));

            // OpenTracing / Sleuth
            logEvent("added: " + remindEntity.getId());
            tagSpan(remindEntity);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new Remind(remindEntity.getNoteReferenceId(), remindEntity.getRemindDateTime()));
    }

    private void tagSpan(RemindEntity noteEntity) {
        tagSpan("id", noteEntity.getId().toString());
        tagSpan("note-reference", String.valueOf(noteEntity.getNoteReferenceId()));
        // TODO: Fix formatting
        tagSpan("remind-date-time", String.valueOf(noteEntity.getRemindDateTime()));
    }

    @DeleteMapping("reminds/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable(value = "id") Long id) {
        try {
            reminderRepo.delete(id);

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

        currentSpan.tag("reminder." + key, content);

    }

    private Span getCurrentSpan() {
        Span span = tracer.getCurrentSpan();
        return tracer.continueSpan(span);
    }
}
