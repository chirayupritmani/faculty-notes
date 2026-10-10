package edu.vit.notes;

import edu.vit.notes.model.AppUser;
import edu.vit.notes.model.Role;
import edu.vit.notes.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserSeedTest {

    @Autowired
    private AppUserRepository users;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    void demoUsersExistWithTheirRoles() {
        assertEquals(Role.FACULTY, users.findByUsername("mehta").orElseThrow().getRole());
        assertEquals(Role.REVIEWER, users.findByUsername("reviewer").orElseThrow().getRole());
        assertEquals(Role.STUDENT, users.findByUsername("student").orElseThrow().getRole());
    }

    @Test
    void passwordsAreStoredHashed() {
        AppUser u = users.findByUsername("mehta").orElseThrow();
        assertNotEquals("mehta123", u.getPasswordHash());
        assertTrue(encoder.matches("mehta123", u.getPasswordHash()));
    }
}
