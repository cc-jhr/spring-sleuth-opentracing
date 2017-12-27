package de.codecentric.opentracing.instana.demo.reminder.rest;

import de.codecentric.opentracing.instana.demo.reminder.dto.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.codecentric.opentracing.instana.demo.reminder.persistence.ReminderEntity;
import de.codecentric.opentracing.instana.demo.reminder.persistence.ReminderRepo;

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

    @GetMapping("/reminders/{id}")
    public ResponseEntity<Reminder> getNoteById(@PathVariable(value = "id") Long id) {
        ReminderEntity remindEntity = reminderRepo.findOne(id);

        if (remindEntity == null) {

            // OpenTracing / Sleuth
            logEvent("reminder not found by id: " + id);

            return ResponseEntity.notFound().build();
        }
        logEvent("reminder found by id: " + id);
        tagSpan(remindEntity);
        return ResponseEntity.ok().body(new Reminder(remindEntity.getNoteReferenceId(), remindEntity.getRemindDateTime()));

    }

    @GetMapping("/reminders")
    public ResponseEntity<List<Reminder>> getAllNotes() {

        List<Reminder> remindList;
        try {
            remindList = reminderRepo.findAll().
                    stream()
                    .map(remindEntity -> new Reminder(remindEntity.getNoteReferenceId(), remindEntity.getRemindDateTime()))
                    .collect(Collectors.toList());

            // OpenTracing / Sleuth
            logEvent("get all reminders, count: " + remindList.size());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(remindList);
    }

    @PostMapping("/reminders")
    public ResponseEntity<Reminder> saveReminder(@RequestBody Reminder remind) {
        ReminderEntity reminderEntity;
        try {
            reminderEntity = reminderRepo.save(new ReminderEntity(remind.getNoteReferenceId(), remind.getRemindDateTime()));

            // OpenTracing / Sleuth
            logEvent("added: " + reminderEntity.getId());
            tagSpan(reminderEntity);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new Reminder(reminderEntity.getNoteReferenceId(), reminderEntity.getRemindDateTime()));
    }

    private void tagSpan(ReminderEntity noteEntity) {
        tagSpan("id", noteEntity.getId().toString());
        tagSpan("note-reference", String.valueOf(noteEntity.getNoteReferenceId()));
        // TODO: Fix formatting
        tagSpan("reminder-date-time", String.valueOf(noteEntity.getRemindDateTime()));
    }

    @DeleteMapping("reminders/{id}")
    public ResponseEntity<String> deleteReminder(@PathVariable(value = "id") Long id) {
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
