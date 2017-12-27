package de.codecentric.opentracing.instana.demo.notebackend.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class NoteRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoteRepo noteRepo;

    @Before
    public void setUp() throws Exception {
        noteRepo.deleteAll();
    }

    @Test
    public void whenFindById_thenReturnNoteEntity() throws Exception {
        //given
        String noteMessage = "test";
        NoteEntity givenNoteEntity = new NoteEntity(noteMessage);
        NoteEntity persistedEntity = entityManager.persist(givenNoteEntity);
        entityManager.flush();

        // when
        NoteEntity foundEntityById = noteRepo.findOne(persistedEntity.getId());

        //then
        assertThat(foundEntityById.getNote(), is(noteMessage));

    }

    @Test
    public void whenDeleteById_thenNumberOfEntitiesIs_ZERO() throws Exception {
        //given
        NoteEntity givenNoteEntity = new NoteEntity();
        NoteEntity persistedEntity = entityManager.persist(givenNoteEntity);
        entityManager.flush();
        int totalSizeBeforeDelete = noteRepo.findAll().size();

        // when
        noteRepo.delete(persistedEntity.getId());
        int totalSizeAfterDelete = noteRepo.findAll().size();

        //then
        assertThat(totalSizeBeforeDelete, is(1));
        assertThat(totalSizeAfterDelete, is(0));

    }

    @Test
    public void whenFindAll_thenNumberOfEntitiesIs_3() throws Exception {
        //given
        entityManager.persist(new NoteEntity());
        entityManager.persist(new NoteEntity());
        entityManager.persist(new NoteEntity());
        entityManager.flush();

        // when
        int totalSize = noteRepo.findAll().size();

        //then
        assertThat(totalSize, is(3));

    }
}