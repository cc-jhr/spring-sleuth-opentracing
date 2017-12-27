package reminder.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author Benjamin Wilms
 */
@Entity
public class RemindEntity {
    public RemindEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long noteReferenceId;

    private LocalDateTime remindDateTime;

    public RemindEntity(Long noteReferenceId, LocalDateTime remindDateTime) {
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

    public LocalDateTime getRemindDateTime() {
        return remindDateTime;
    }

    public void setRemindDateTime(LocalDateTime remindDateTime) {
        this.remindDateTime = remindDateTime;
    }
}
