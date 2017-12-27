package reminder.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ReminderRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReminderRepo reminderRepo;

    @Before
    public void setUp() throws Exception {
        reminderRepo.deleteAll();
    }

    @Test
    public void whenFindById_thenReturnRemindEntity() throws Exception {
        //given
        Long referencedNoteId = 99l;
        LocalDateTime localDateTime = LocalDateTime.now();
        RemindEntity givenRemindEntity = new RemindEntity(referencedNoteId, localDateTime);
        RemindEntity persistedEntity = entityManager.persist(givenRemindEntity);
        entityManager.flush();

        // when
        RemindEntity foundEntityById = reminderRepo.findOne(persistedEntity.getId());

        //then
        assertThat(foundEntityById.getNoteReferenceId(), is(referencedNoteId));
        assertThat(foundEntityById.getRemindDateTime(), is(localDateTime));

    }

    @Test
    public void whenDeleteById_thenNumberOfEntitiesIs_ZERO() throws Exception {
        //given
        RemindEntity givenRemindEntity = new RemindEntity();
        RemindEntity persistedEntity = entityManager.persist(givenRemindEntity);
        entityManager.flush();
        int totalSizeBeforeDelete = reminderRepo.findAll().size();

        // when
        reminderRepo.delete(persistedEntity.getId());
        int totalSizeAfterDelete = reminderRepo.findAll().size();

        //then
        assertThat(totalSizeBeforeDelete, is(1));
        assertThat(totalSizeAfterDelete, is(0));

    }

    @Test
    public void whenFindAll_thenNumberOfEntitiesIs_3() throws Exception {
        //given
        entityManager.persist(new RemindEntity());
        entityManager.persist(new RemindEntity());
        entityManager.persist(new RemindEntity());
        entityManager.flush();

        // when
        int totalSize = reminderRepo.findAll().size();

        //then
        assertThat(totalSize, is(3));

    }

}