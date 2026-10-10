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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "mehta", roles = "FACULTY")
class NoteViewEditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository repository;

    private Note saveNote(NoteStatus status) {
        Note n = new Note();
        n.setTitle("Original title");
        n.setSubject("DevOps");
        n.setContent("Original content");
        n.setAuthor("Prof. Mehta");
        n.setStatus(status);
        return repository.save(n);
    }

    @Test
    void viewShowsFullNote() throws Exception {
        Note n = saveNote(NoteStatus.DRAFT);

        mockMvc.perform(get("/notes/{id}", n.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Original title")))
                .andExpect(content().string(containsString("Original content")))
                .andExpect(content().string(containsString("Prof. Mehta")));
    }

    @Test
    void viewUnknownNoteReturns404() throws Exception {
        mockMvc.perform(get("/notes/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void draftNoteCanBeEdited() throws Exception {
        Note n = saveNote(NoteStatus.DRAFT);

        mockMvc.perform(post("/notes/{id}", n.getId())
                        .with(csrf())
                        .param("title", "Updated title")
                        .param("subject", "Networks")
                        .param("author", "Someone Else")
                        .param("content", "Updated content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes/" + n.getId()));

        Note saved = repository.findById(n.getId()).orElseThrow();
        assertEquals("Updated title", saved.getTitle());
        assertEquals("Networks", saved.getSubject());
        assertEquals("Updated content", saved.getContent());
        assertEquals("Prof. Mehta", saved.getAuthor());      // author is not editable
        assertEquals(NoteStatus.DRAFT, saved.getStatus());
    }

    @Test
    void editWithBlankTitleShowsValidationError() throws Exception {
        Note n = saveNote(NoteStatus.DRAFT);

        mockMvc.perform(post("/notes/{id}", n.getId())
                        .with(csrf())
                        .param("title", "")
                        .param("subject", "DevOps")
                        .param("author", "Prof. Mehta")
                        .param("content", "Some content"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title is required")));

        assertEquals("Original title", repository.findById(n.getId()).orElseThrow().getTitle());
    }

    @Test
    void publishedNoteCannotBeEdited() throws Exception {
        Note n = saveNote(NoteStatus.PUBLISHED);

        mockMvc.perform(post("/notes/{id}", n.getId())
                        .with(csrf())
                        .param("title", "Hacked title")
                        .param("subject", "DevOps")
                        .param("author", "Prof. Mehta")
                        .param("content", "Hacked content"))
                .andExpect(status().is3xxRedirection());

        assertEquals("Original title", repository.findById(n.getId()).orElseThrow().getTitle());
    }
}
