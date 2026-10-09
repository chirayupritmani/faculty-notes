package edu.vit.notes.repository;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByOrderByUpdatedAtDesc();

    List<Note> findByTitleContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
            String title, String subject, String content);

    long countByStatus(NoteStatus status);
}
