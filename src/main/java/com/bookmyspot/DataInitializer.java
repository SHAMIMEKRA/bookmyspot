package com.bookmyspot;

import com.bookmyspot.model.Role;
import com.bookmyspot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// This @Component makes it a Spring Bean
// CommandLineRunner means it will run() once on application startup
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if "ROLE_USER" already exists in the database
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            System.out.println("CREATED: ROLE_USER");
        }

        // Check if "ROLE_ADMIN" already exists
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("CREATED: ROLE_ADMIN");
        }
    }
}