package com.example.back.service;

import com.example.back.domain.softdelete.UserDeletedAtSoftDeleteHandler;
import com.example.back.dto.request.CreateUserRequest;
import com.example.back.dto.request.UpdateUserRequest;
import com.example.back.dto.request.UpdateUserStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.UserResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.UserMapper;
import com.example.back.model.User;
import com.example.back.model.UserStatus;
import com.example.back.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDeletedAtSoftDeleteHandler softDeleteHandler;

    public UserService(
            IUserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            UserDeletedAtSoftDeleteHandler softDeleteHandler) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.softDeleteHandler = softDeleteHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(String q, Boolean activeOnly, Pageable pageable) {
        Page<User> page = userRepository.search(q, activeOnly, pageable);
        return userMapper.toPage(page);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(requireUser(id));
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Cet e-mail est déjà utilisé");
        }

        User user = new User(
                request.getName().trim(),
                email,
                passwordEncoder.encode(request.getPassword()));
        user.assignRole(request.getRole() == null ? "USER" : request.getRole());

        User saved = userRepository.save(user);
        log.info("User created id={} email={} role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = requireUser(id);

        if (request.getName() != null) {
            user.rename(request.getName());
        }
        if (request.getEmail() != null) {
            String email = request.getEmail().trim().toLowerCase();
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new BusinessException("Cet e-mail est déjà utilisé");
            }
            user.changeEmail(email);
        }
        if (request.getRole() != null) {
            user.assignRole(request.getRole());
        }

        User saved = userRepository.save(user);
        log.info("User updated id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request, Long actorUserId) {
        User user = requireUser(id);

        if (actorUserId != null && actorUserId.equals(id) && request.getStatus() == UserStatus.INACTIVE) {
            throw new BusinessException("Un administrateur ne peut pas se désactiver lui-même");
        }

        if (request.getStatus() == UserStatus.INACTIVE) {
            softDeleteHandler.softDelete(user);
        } else {
            softDeleteHandler.restore(user);
        }

        User saved = userRepository.save(user);
        log.info("User status id={} -> {}", saved.getId(), UserStatus.fromUser(saved));
        return userMapper.toResponse(saved);
    }

    private User requireUser(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
