package edu.vit.notes;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "mehta", roles = "FACULTY")
class DashboardSubjectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository repository;

    private void saveNote(String subject) {
        Note n = new Note();
        n.setTitle("Dashboard note");
        n.setSubject(subject);
        n.setContent("Content");
        n.setAuthor("Prof. Mehta");
        n.setStatus(NoteStatus.DRAFT);
        repository.save(n);
    }

    @Test
    void countsNotesPerSubject() {
        saveNote("Zeta Subject");
        saveNote("Zeta Subject");

        long total = repository.countBySubject().stream()
                .filter(s -> s.getSubject().equals("Zeta Subject"))
                .findFirst().orElseThrow().getTotal();

        assertEquals(2, total);
    }

    @Test
    void dashboardShowsSubjectTable() throws Exception {
        saveNote("Eta Subject");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Notes by subject")))
                .andExpect(content().string(containsString("Eta Subject")));
    }
}
