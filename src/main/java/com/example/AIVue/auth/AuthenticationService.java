package com.example.AIVue.auth;

import com.example.AIVue.Repository.UserRepository;
import com.example.AIVue.config.JWTService;
import com.example.AIVue.dto.auth.AuthResponse;
import com.example.AIVue.dto.auth.LoginRequest;
import com.example.AIVue.dto.auth.RegisterRequest;
import com.example.AIVue.model.Role;
import com.example.AIVue.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

//    @Transactional
    public AuthResponse register(RegisterRequest request) throws Exception {

        if(repository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already registered");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        var userOptional = repository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Invalid email");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid password");
        }

        var user = userOptional.get();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}
