package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateUserRequest;
import org.example.helpdesk.dto.UserResponse;
import org.example.helpdesk.entity.User;
import org.example.helpdesk.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public UserResponse update(Long id, CreateUserRequest request) {
        User user = findEntityById(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = findEntityById(id);
        userRepository.delete(user);
    }

    protected User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }
}
