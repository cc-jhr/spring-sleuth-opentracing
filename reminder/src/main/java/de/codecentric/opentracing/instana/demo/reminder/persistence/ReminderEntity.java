package de.codecentric.opentracing.instana.demo.reminder.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Benjamin Wilms
 */
@Entity
public class ReminderEntity {
    public ReminderEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long noteReferenceId;

    private Date remindDateTime;

    public ReminderEntity(Long noteReferenceId, Date remindDateTime) {
        this.noteReferenceId = noteReferenceId;
        this.remindDateTime = remindDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
