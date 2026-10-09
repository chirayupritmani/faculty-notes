package edu.vit.notes.web;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Skeleton controller: list, search and create.
 * View one, update and the status workflow are added in Tasks 5 and 6.
 */
@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository notes;

    public NoteController(NoteRepository notes) {
        this.notes = notes;
    }

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q, Model model) {
        String term = q == null ? "" : q.trim();
        List<Note> result = term.isEmpty()
                ? notes.findAllByOrderByUpdatedAtDesc()
                : notes.findByTitleContainingIgnoreCaseOrSubjectContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
                        term, term, term);
        model.addAttribute("notes", result);
        model.addAttribute("q", term);
        return "notes/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("note", new Note());
        return "notes/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("note") Note note, BindingResult result) {
        if (result.hasErrors()) {
            return "notes/form";
        }
        note.setId(null);
        note.setStatus(NoteStatus.DRAFT);
        notes.save(note);
        return "redirect:/notes";
    }
}
