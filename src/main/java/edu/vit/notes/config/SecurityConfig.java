package edu.vit.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Access rules:
 * - everyone logged in can view pages;
 * - only Faculty can create, edit and submit notes;
 * - only Reviewers can approve or return notes.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/error", "/actuator/health", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/notes/new", "/notes/*/edit").hasRole("FACULTY")
                .requestMatchers(HttpMethod.POST, "/notes", "/notes/*", "/notes/*/submit").hasRole("FACULTY")
                .requestMatchers(HttpMethod.POST, "/notes/*/approve", "/notes/*/return").hasRole("REVIEWER")
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
