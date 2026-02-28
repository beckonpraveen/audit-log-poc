package org.example.helpdesk.auth.service;

import java.util.Set;
import org.example.helpdesk.auth.dto.AuthResponse;
import org.example.helpdesk.auth.dto.LoginRequest;
import org.example.helpdesk.auth.dto.RegisterRequest;
import org.example.helpdesk.entity.User;
import org.example.helpdesk.entity.UserRole;
import org.example.helpdesk.repository.UserRepository;
import org.example.helpdesk.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(UserRole.AGENT));

        User saved = userRepository.save(user);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(saved.getUsername())
                .password(saved.getPasswordHash())
                .authorities(saved.getRoles().stream().map(role -> "ROLE_" + role.name()).toArray(String[]::new))
                .build();

        return new AuthResponse(jwtService.generateToken(userDetails));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(userDetails));
    }
}
