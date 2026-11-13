package com.example.medique.config;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return email -> {
            User user = users.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/user/**").hasRole("PATIENT")
                    .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true).clearAuthentication(true)
                    .deleteCookies("JSESSIONID").permitAll())
            .sessionManagement(session -> session.sessionFixation(fixation -> fixation.migrateSession())
                    .maximumSessions(1).maxSessionsPreventsLogin(false).expiredUrl("/login?expired"));
        // CSRF protection remains enabled for browser forms.
        return http.build();
    }
}
