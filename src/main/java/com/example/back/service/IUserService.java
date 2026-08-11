package com.example.back.service;

import com.example.back.dto.request.CreateUserRequest;
import com.example.back.dto.request.UpdateUserRequest;
import com.example.back.dto.request.UpdateUserStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    PageResponse<UserResponse> listUsers(String q, Boolean activeOnly, Pageable pageable);

    UserResponse getUser(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse updateStatus(Long id, UpdateUserStatusRequest request, Long actorUserId);
}
