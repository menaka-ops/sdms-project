package com.example.sdms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    // Inject the custom success handler you already built
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(RoleBasedAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        // Allow access to static resources and public pages
                        .requestMatchers(
                                "/", // Allow root
                                "/index.html", // Allow new login page
                                "/register.html", // Allow new register page
                                "/style.css",
                                "/api/auth/**" // Allow registration API
                        ).permitAll()
                        // Require authentication for any other request
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/index.html") // Your new unified login page
                        .loginProcessingUrl("/login") // Spring Security's login endpoint
                        .successHandler(authenticationSuccessHandler) // Use your role-based redirect
                        .failureUrl("/index.html?error=true") // Redirect back on failure
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index.html?logout=true") // Redirect to login on logout
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}