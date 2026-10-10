package edu.vit.notes.config;

import edu.vit.notes.model.AppUser;
import edu.vit.notes.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Loads login accounts from the app_users table. */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository users;

    public AppUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRole().name())
                .build();
    }
}
