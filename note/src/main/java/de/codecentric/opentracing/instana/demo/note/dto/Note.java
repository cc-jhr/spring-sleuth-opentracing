package de.codecentric.opentracing.instana.demo.note.dto;

/**
 * @author Benjamin Wilms
 */
public class Note {

    private Long id;
    private String noteMessage;

    public Note() {
    }

    public Note(Long id, String noteMessage) {
        this.id = id;
        this.noteMessage = noteMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoteMessage() {
        return noteMessage;
    }

    public void setNoteMessage(String noteMessage) {
        this.noteMessage = noteMessage;
    }
}
