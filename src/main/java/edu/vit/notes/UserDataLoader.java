package edu.vit.notes;

import edu.vit.notes.model.AppUser;
import edu.vit.notes.model.Role;
import edu.vit.notes.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adds demo accounts on startup (the database is in memory and resets on every restart).
 * Demo passwords are for coursework only.
 */
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public UserDataLoader(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }
        add("mehta", "mehta123", "Prof. Mehta", Role.FACULTY);
        add("iyer", "iyer123", "Prof. Iyer", Role.FACULTY);
        add("reviewer", "review123", "Dr. Rao", Role.REVIEWER);
        add("student", "student123", "Student User", Role.STUDENT);
    }

    private void add(String username, String rawPassword, String displayName, Role role) {
        users.save(new AppUser(username, encoder.encode(rawPassword), displayName, role));
    }
}
