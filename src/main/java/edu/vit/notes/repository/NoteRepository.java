package edu.vit.notes.repository;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /** One row of the dashboard's "notes by subject" table. */
    interface SubjectCount {
        String getSubject();

        long getTotal();
    }

    List<Note> findAllByOrderByUpdatedAtDesc();

    List<Note> findByTitleContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
            String title, String subject, String content);

    long countByStatus(NoteStatus status);

    @Query("select n.subject as subject, count(n) as total from Note n group by n.subject order by n.subject")
    List<SubjectCount> countBySubject();
}
