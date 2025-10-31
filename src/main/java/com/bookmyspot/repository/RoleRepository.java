package com.bookmyspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookmyspot.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(String name);
}
