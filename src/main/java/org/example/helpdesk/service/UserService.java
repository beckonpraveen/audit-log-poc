package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateUserRequest;
import org.example.helpdesk.dto.UserResponse;

public interface UserService {
    UserResponse create(CreateUserRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, CreateUserRequest request);

    void delete(Long id);
}
