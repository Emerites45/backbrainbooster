package com.example.back.repository;

import com.example.back.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements IUserRepository {

    private final UserRepository jpa;

    public UserRepositoryAdapter(UserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpa.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public Page<User> search(String q, Boolean activeOnly, Pageable pageable) {
        boolean onlyActive = activeOnly == null || activeOnly;
        String query = (q == null || q.isBlank()) ? null : q.trim();
        return jpa.search(query, onlyActive, pageable);
    }

    @Override
    public User save(User user) {
        return jpa.save(user);
    }
}
