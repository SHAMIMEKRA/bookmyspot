package com.bookmyspot.cnfig;


import com.bookmyspot.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // This enables method-level security (like @PreAuthorize)
public class SecurityConfig {

    // We will create the JwtAuthFilter in the next step, so this will be red for now.
     @Autowired
     private JwtAuthFilter authFilter;
     @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Bean #1: The Password Encoder
    // This is used to hash and verify passwords
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
    // Bean #2: The Security Filter Chain (The "Rulebook")
    // This is the main configuration for what's public and what's protected.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf->csrf.disable()) // Disable CSRF protection (common for stateless APIs)

                // This is the "rulebook" for our API endpoints
                .authorizeHttpRequests(
                        authz->authz
                                // 1. Allow all requests to our auth endpoints
                                .requestMatchers("/api/auth/**").permitAll()
                                // 2. All other requests must be authenticated
                                .anyRequest().authenticated()
                )
                // 3. Set session management to STATELESS
                // We're using JWTs, so we don't need the server to store session cookies.
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Tell Spring Security how to find our user details
                .authenticationProvider(authenticationProvider())

                // 5. We will add our JWT filter here in the next step
                // .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();



    }

    // Bean #3: The Authentication Provider
    // This tells Spring Security to use our UserDetailsServiceImpl and our PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // How to find the user
        authProvider.setPasswordEncoder(passwordEncoder());// How to check the password

        return authProvider;
    }

    // Bean #4: The Authentication Manager
    // This is the "boss" that manages the authentication process. We'll use this in our AuthController.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }



}
