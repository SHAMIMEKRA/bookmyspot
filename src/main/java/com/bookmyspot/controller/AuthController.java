package com.bookmyspot.controller;


import com.bookmyspot.dto.JwtResponse;
import com.bookmyspot.dto.LoginRequest;
import com.bookmyspot.dto.SignUpRequest;

import com.bookmyspot.repository.RoleRepository;
import com.bookmyspot.repository.UserRepository;
import com.bookmyspot.service.JwtService;
import com.bookmyspot.model.Role;
import com.bookmyspot.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
// This lets our React app (on a different port) call these APIs
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager; // The "boss" of authentication

    @Autowired
    UserRepository userRepository; // To save/find users

    @Autowired
    RoleRepository roleRepository; // To find roles

    @Autowired
    PasswordEncoder passwordEncoder; // To hash passwords

    @Autowired
    JwtService jwtService; // Our "Key Maker"

    // === The LOGIN Endpoint ===

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

        // 1. Authenticate the user (check username and password)
        // This uses our UserDetailsServiceImpl and PasswordEncoder
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. If successful, generate a JWT token using our JwtService
        String jwt = jwtService.generateToken(loginRequest.getUsername());

        // 3. Get UserDetails to send back to the client
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 4. Find the full User object to get ID and Email
        User user = userRepository.findByUsername(userDetails.getUsername()).get();

        // 5. Respond with the JWT token and user info (in our DTO)
        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles));

    }

    // === The REGISTER Endpoint ===
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        // 1. Check if username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        // 2. Check if email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // 3. Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword())); // Hash the password!

        // 4. Assign the default role "ROLE_USER"
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role 'ROLE_USER' is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        // 5. Save the user to the database
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }






}
