package reminder.dto;

import java.time.LocalDateTime;

/**
 * @author Benjamin Wilms
 */
public class Remind {

    private Long noteReferenceId;
    private LocalDateTime remindDateTime;

    public Remind() {
    }

    public Remind(Long noteReferenceId, LocalDateTime remindDateTime) {
        this.noteReferenceId = noteReferenceId;
        this.remindDateTime = remindDateTime;
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
