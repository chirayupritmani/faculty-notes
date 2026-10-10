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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository repository;

    private Note saveNote(String author, NoteStatus status) {
        Note n = new Note();
        n.setTitle("Workflow note");
        n.setSubject("DevOps");
        n.setContent("Some content");
        n.setAuthor(author);
        n.setStatus(status);
        return repository.save(n);
    }

    private NoteStatus statusOf(Note n) {
        return repository.findById(n.getId()).orElseThrow().getStatus();
    }

    @Test
    @WithMockUser(username = "mehta", roles = "FACULTY")
    void authorCanSubmitOwnDraft() throws Exception {
        Note n = saveNote("Prof. Mehta", NoteStatus.DRAFT);
        mockMvc.perform(post("/notes/{id}/submit", n.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertEquals(NoteStatus.UNDER_REVIEW, statusOf(n));
    }

    @Test
    @WithMockUser(username = "mehta", roles = "FACULTY")
    void facultyCannotSubmitSomeoneElsesNote() throws Exception {
        Note n = saveNote("Prof. Iyer", NoteStatus.DRAFT);
        mockMvc.perform(post("/notes/{id}/submit", n.getId()).with(csrf()))
                .andExpect(status().isForbidden());
        assertEquals(NoteStatus.DRAFT, statusOf(n));
    }

    @Test
    @WithMockUser(username = "reviewer", roles = "REVIEWER")
    void reviewerCanApprove() throws Exception {
        Note n = saveNote("Prof. Mehta", NoteStatus.UNDER_REVIEW);
        mockMvc.perform(post("/notes/{id}/approve", n.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection());
        assertEquals(NoteStatus.PUBLISHED, statusOf(n));
    }

    @Test
    @WithMockUser(username = "reviewer", roles = "REVIEWER")
    void reviewerCanReturnWithComment() throws Exception {
        Note n = saveNote("Prof. Mehta", NoteStatus.UNDER_REVIEW);
        mockMvc.perform(post("/notes/{id}/return", n.getId()).with(csrf()).param("comment", "Add examples"))
                .andExpect(status().is3xxRedirection());
        Note saved = repository.findById(n.getId()).orElseThrow();
        assertEquals(NoteStatus.DRAFT, saved.getStatus());
        assertEquals("Add examples", saved.getReviewComment());
    }

    @Test
    @WithMockUser(username = "reviewer", roles = "REVIEWER")
    void returnWithoutCommentKeepsStatus() throws Exception {
        Note n = saveNote("Prof. Mehta", NoteStatus.UNDER_REVIEW);
        mockMvc.perform(post("/notes/{id}/return", n.getId()).with(csrf()).param("comment", " "))
                .andExpect(status().is3xxRedirection());
        assertEquals(NoteStatus.UNDER_REVIEW, statusOf(n));
    }

    @Test
    @WithMockUser(username = "mehta", roles = "FACULTY")
    void facultyCannotApprove() throws Exception {
        Note n = saveNote("Prof. Mehta", NoteStatus.UNDER_REVIEW);
        mockMvc.perform(post("/notes/{id}/approve", n.getId()).with(csrf()))
                .andExpect(status().isForbidden());
        assertEquals(NoteStatus.UNDER_REVIEW, statusOf(n));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    void studentCannotCreateNotes() throws Exception {
        mockMvc.perform(post("/notes").with(csrf())
                        .param("title", "x").param("subject", "y").param("author", "z").param("content", "c"))
                .andExpect(status().isForbidden());
    }
}
