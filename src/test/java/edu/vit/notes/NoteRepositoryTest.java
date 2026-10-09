package edu.vit.notes;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class NoteRepositoryTest {

    @Autowired
    private NoteRepository repository;

    private Note newNote(String title, String subject) {
        Note n = new Note();
        n.setTitle(title);
        n.setSubject(subject);
        n.setContent("Sample content");
        n.setAuthor("Prof. Test");
        return n;
    }

    @Test
    void searchFindsNoteBySubjectIgnoringCase() {
        repository.save(newNote("Unit 1", "DevOps"));
        repository.save(newNote("Unit 2", "Networks"));

        List<Note> found = repository
                .findByTitleContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
                        "devops", "devops", "devops");

        assertEquals(1, found.size());
        assertEquals("Unit 1", found.get(0).getTitle());
    }

    @Test
    void newNoteStartsAsDraftAndIsCounted() {
        repository.save(newNote("Unit 3", "DevOps"));

        assertEquals(1, repository.countByStatus(NoteStatus.DRAFT));
        assertEquals(0, repository.countByStatus(NoteStatus.PUBLISHED));
    }
}
