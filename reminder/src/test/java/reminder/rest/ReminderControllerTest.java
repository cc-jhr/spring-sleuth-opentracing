package reminder.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Span;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import reminder.persistence.RemindEntity;
import reminder.persistence.ReminderRepo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ReminderController.class)
public class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReminderRepo reminderRepoMock;

    @MockBean
    private org.springframework.cloud.sleuth.Tracer tracerMock;

    @Test
    public void findAllNotes_Goodcase() throws Exception {

        RemindEntity remindEntity = new RemindEntity(99l, LocalDateTime.now());
        remindEntity.setId(1l);

        RemindEntity remindEntityTwo = new RemindEntity(98l, LocalDateTime.now());
        remindEntityTwo.setId(2l);

        List<RemindEntity> reminds = Arrays.asList(remindEntity, remindEntityTwo);

        given(reminderRepoMock.findAll()).willReturn(reminds);
        Span span = Span.builder().build();
        given(tracerMock.getCurrentSpan()).willReturn(span);
        given(tracerMock.continueSpan(span)).willReturn(span);

        mockMvc.perform(get("/reminds").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].noteReferenceId", is(remindEntity.getNoteReferenceId().intValue())))
                .andExpect(jsonPath("$[1].noteReferenceId", is(remindEntityTwo.getNoteReferenceId().intValue())));
    }
}