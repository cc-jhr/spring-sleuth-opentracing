package de.codecentric.opentracing.instana.demo.reminder.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.opentracing.instana.demo.reminder.dto.Reminder;
import de.codecentric.opentracing.instana.demo.reminder.persistence.ReminderEntity;
import de.codecentric.opentracing.instana.demo.reminder.persistence.ReminderRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Span;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Before
    public void setUp() throws Exception {
        Span span = Span.builder().build();
        given(tracerMock.getCurrentSpan()).willReturn(span);
        given(tracerMock.continueSpan(span)).willReturn(span);
    }

    @Test
    public void findAllNotes_Goodcase() throws Exception {

        ReminderEntity reminderEntity = new ReminderEntity(99l, new Date());
        reminderEntity.setId(1l);

        ReminderEntity reminderEntityTwo = new ReminderEntity(98l, new Date());
        reminderEntityTwo.setId(2l);

        List<ReminderEntity> reminds = Arrays.asList(reminderEntity, reminderEntityTwo);

        given(reminderRepoMock.findAll()).willReturn(reminds);

        mockMvc.perform(get("/reminders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].noteReferenceId", is(reminderEntity.getNoteReferenceId().intValue())))
                .andExpect(jsonPath("$[1].noteReferenceId", is(reminderEntityTwo.getNoteReferenceId().intValue())));
    }


    @Test
    public void saveReminder_Goodcase() throws Exception {

        Reminder reminder = new Reminder(1l, new Date());

        ReminderEntity reminderEntity = new ReminderEntity(reminder.getNoteReferenceId(), reminder.getRemindDateTime());
        reminderEntity.setId(99l);

        given(reminderRepoMock.save(Matchers.any(ReminderEntity.class))).willReturn(reminderEntity);

        mockMvc.perform(post("/reminders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(reminder))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteReferenceId", is(reminder.getNoteReferenceId().intValue())))
        ;
    }

    @Test
    public void deleteReminder_Goodcase() throws Exception {
        int idToDelete = 1;

        mockMvc.perform(delete("/reminders/" + idToDelete)).andExpect(status().isOk());
    }

    @Test
    public void deleteReminder_Badcase() throws Exception {
        int idToDelete = 1;

        doThrow(new RuntimeException()).when(reminderRepoMock).delete(Matchers.any(Long.class));

        mockMvc.perform(delete("/reminders/" + idToDelete)).andExpect(status().isBadRequest());
    }

    private String json(Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }
}