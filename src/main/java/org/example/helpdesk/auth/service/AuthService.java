package org.example.helpdesk.auth.service;

import org.example.helpdesk.auth.dto.AuthResponse;
import org.example.helpdesk.auth.dto.LoginRequest;
import org.example.helpdesk.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
