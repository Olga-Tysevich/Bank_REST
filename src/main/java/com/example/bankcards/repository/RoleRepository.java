package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface provides methods for accessing and manipulating Role entities in the database.
 *
 * @see Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Retrieves a Role entity by RoleEnum value.
     *
     * @param role The role of the user associated with the role enum.
     * @return The user Role from database.
     */
    Optional<Role> getByRole(RoleEnum role);

}