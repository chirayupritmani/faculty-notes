package edu.vit.notes.web;

import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Notes controller: list, search, create (skeleton), view one and edit a Draft (Task 5).
 * The status workflow and role checks are added in Task 6.
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

    // ---- Task 5: view one note (FR-04) ----

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("note", findOr404(id));
        return "notes/detail";
    }

    // ---- Task 5: edit own note while Draft (FR-05) ----

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Note note = findOr404(id);
        if (note.getStatus() != NoteStatus.DRAFT) {
            return "redirect:/notes/" + id;
        }
        model.addAttribute("note", note);
        return "notes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("note") Note form,
                         BindingResult result) {
        Note existing = findOr404(id);
        if (existing.getStatus() != NoteStatus.DRAFT) {
            return "redirect:/notes/" + id;
        }
        if (result.hasErrors()) {
            form.setId(id);
            form.setStatus(existing.getStatus());
            return "notes/edit";
        }
        // Only these three fields can change; author, status and dates stay as stored.
        existing.setTitle(form.getTitle());
        existing.setSubject(form.getSubject());
        existing.setContent(form.getContent());
        notes.save(existing);
        return "redirect:/notes/" + id;
    }

    private Note findOr404(Long id) {
        return notes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }
}
