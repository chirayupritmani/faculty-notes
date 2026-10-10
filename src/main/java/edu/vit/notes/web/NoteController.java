package edu.vit.notes.web;

import edu.vit.notes.model.AppUser;
import edu.vit.notes.model.Note;
import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.AppUserRepository;
import edu.vit.notes.repository.NoteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Notes controller: list, search, create, view, edit, and the status workflow
 * Draft -> Under Review -> Published (or back to Draft with a reviewer comment).
 * Role rules are set in SecurityConfig; "own note" checks are done here.
 */
@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository notes;
    private final AppUserRepository users;

    public NoteController(NoteRepository notes, AppUserRepository users) {
        this.notes = notes;
        this.users = users;
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
    public String newForm(Model model, Authentication auth) {
        Note note = new Note();
        note.setAuthor(displayName(auth));
        model.addAttribute("note", note);
        return "notes/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("note") Note note, BindingResult result, Authentication auth) {
        if (result.hasErrors()) {
            return "notes/form";
        }
        note.setId(null);
        note.setAuthor(displayName(auth));
        note.setStatus(NoteStatus.DRAFT);
        note.setReviewComment(null);
        notes.save(note);
        return "redirect:/notes";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, Authentication auth) {
        Note note = findOr404(id);
        boolean owner = isAuthor(note, auth);
        model.addAttribute("note", note);
        model.addAttribute("canEdit", owner && note.getStatus() == NoteStatus.DRAFT);
        model.addAttribute("canReview", hasRole(auth, "REVIEWER") && note.getStatus() == NoteStatus.UNDER_REVIEW);
        return "notes/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        Note note = findOr404(id);
        requireAuthor(note, auth);
        if (note.getStatus() != NoteStatus.DRAFT) {
            return "redirect:/notes/" + id;
        }
        model.addAttribute("note", note);
        return "notes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("note") Note form,
                         BindingResult result,
                         Authentication auth) {
        Note existing = findOr404(id);
        requireAuthor(existing, auth);
        if (existing.getStatus() != NoteStatus.DRAFT) {
            return "redirect:/notes/" + id;
        }
        if (result.hasErrors()) {
            form.setId(id);
            form.setStatus(existing.getStatus());
            return "notes/edit";
        }
        existing.setTitle(form.getTitle());
        existing.setSubject(form.getSubject());
        existing.setContent(form.getContent());
        notes.save(existing);
        return "redirect:/notes/" + id;
    }

    // ---- status workflow ----

    @PostMapping("/{id}/submit")
    public String submit(@PathVariable Long id, Authentication auth) {
        Note note = findOr404(id);
        requireAuthor(note, auth);
        if (note.getStatus() == NoteStatus.DRAFT) {
            note.setStatus(NoteStatus.UNDER_REVIEW);
            note.setReviewComment(null);
            notes.save(note);
        }
        return "redirect:/notes/" + id;
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id) {
        Note note = findOr404(id);
        if (note.getStatus() == NoteStatus.UNDER_REVIEW) {
            note.setStatus(NoteStatus.PUBLISHED);
            note.setReviewComment(null);
            notes.save(note);
        }
        return "redirect:/notes/" + id;
    }

    @PostMapping("/{id}/return")
    public String returnToAuthor(@PathVariable Long id,
                                 @RequestParam(name = "comment", required = false) String comment,
                                 RedirectAttributes redirect) {
        Note note = findOr404(id);
        if (note.getStatus() == NoteStatus.UNDER_REVIEW) {
            if (comment == null || comment.isBlank()) {
                redirect.addFlashAttribute("error", "A comment is required to return a note.");
            } else {
                note.setStatus(NoteStatus.DRAFT);
                note.setReviewComment(comment.trim());
                notes.save(note);
            }
        }
        return "redirect:/notes/" + id;
    }

    // ---- helpers ----

    private Note findOr404(Long id) {
        return notes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    private String displayName(Authentication auth) {
        return users.findByUsername(auth.getName()).map(AppUser::getDisplayName).orElse(auth.getName());
    }

    private boolean isAuthor(Note note, Authentication auth) {
        return users.findByUsername(auth.getName())
                .map(u -> u.getDisplayName().equals(note.getAuthor()))
                .orElse(false);
    }

    private void requireAuthor(Note note, Authentication auth) {
        if (!isAuthor(note, auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author can do this");
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
