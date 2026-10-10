package edu.vit.notes.web;

import edu.vit.notes.model.NoteStatus;
import edu.vit.notes.repository.NoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NoteRepository notes;

    public HomeController(NoteRepository notes) {
        this.notes = notes;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("draftCount", notes.countByStatus(NoteStatus.DRAFT));
        model.addAttribute("reviewCount", notes.countByStatus(NoteStatus.UNDER_REVIEW));
        model.addAttribute("publishedCount", notes.countByStatus(NoteStatus.PUBLISHED));
        model.addAttribute("totalCount", notes.count());
        model.addAttribute("subjectCounts", notes.countBySubject());
        return "index";
    }
}
