package com.example.back.repository;

import com.example.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link User} entity.
 * Provides standard CRUD operations and custom database queries for User
 * management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     * key functionality for Authentication (Login and registration checks).
     *
     * @param email The email address to search for.
     * @return An {@link Optional} containing the user if found, or empty if not.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email.
     * Used during signup to prevent duplicate accounts.
     *
     * @param email The email to check.
     * @return true if the email is already registered, false otherwise.
     */
    boolean existsByEmail(String email);
}