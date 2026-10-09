package edu.vit.notes;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Adds a few sample notes on startup so the dashboard and search have something to show. */
@Component
public class DataLoader implements CommandLineRunner {

    private final NoteRepository notes;

    public DataLoader(NoteRepository notes) {
        this.notes = notes;
    }

    @Override
    public void run(String... args) {
        if (notes.count() > 0) {
            return;
        }
        notes.save(sample("Introduction to DevOps", "DevOps",
                "Culture, practices and tools that join development and operations.", "Prof. Mehta", NoteStatus.PUBLISHED));
        notes.save(sample("Git Branching Basics", "DevOps",
                "Feature branches, pull requests and merge strategies.", "Prof. Mehta", NoteStatus.UNDER_REVIEW));
        notes.save(sample("Normalisation in DBMS", "Database Systems",
                "1NF, 2NF, 3NF and BCNF with worked examples.", "Prof. Iyer", NoteStatus.DRAFT));
    }

    private Note sample(String title, String subject, String content, String author, NoteStatus status) {
        Note n = new Note();
        n.setTitle(title);
        n.setSubject(subject);
        n.setContent(content);
        n.setAuthor(author);
        n.setStatus(status);
        return n;
    }
}
