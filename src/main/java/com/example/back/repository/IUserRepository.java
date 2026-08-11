package com.example.back.repository;

import com.example.back.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Port repository USER (DIP). */
public interface IUserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<User> search(String q, Boolean activeOnly, Pageable pageable);

    User save(User user);
}
