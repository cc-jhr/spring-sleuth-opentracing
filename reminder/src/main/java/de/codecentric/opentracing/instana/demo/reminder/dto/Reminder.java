package de.codecentric.opentracing.instana.demo.reminder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Benjamin Wilms
 */
public class Reminder {

    private Long noteReferenceId;

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public Date remindDateTime;

    public Reminder() {
    }

    public Reminder(Long noteReferenceId, Date remindDateTime) {
        this.noteReferenceId = noteReferenceId;
        this.remindDateTime = remindDateTime;
    }

    public Long getNoteReferenceId() {
        return noteReferenceId;
    }

    public void setNoteReferenceId(Long noteReferenceId) {
        this.noteReferenceId = noteReferenceId;
    }

    public Date getRemindDateTime() {
        return remindDateTime;
    }

    public void setRemindDateTime(Date remindDateTime) {
        this.remindDateTime = remindDateTime;
    }
}
