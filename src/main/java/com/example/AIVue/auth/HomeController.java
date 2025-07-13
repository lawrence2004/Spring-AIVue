package com.example.AIVue.auth;


import com.example.AIVue.dto.auth.AuthResponse;
import com.example.AIVue.dto.auth.LoginRequest;
import com.example.AIVue.dto.auth.RegisterRequest;
import com.example.AIVue.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
//@Tag(name = "Authentication", description = "Endpoints for User Registration and Login")
public class HomeController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(401)
                    .body(e.getMessage()); // Invalid email or password
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Something went wrong");
        }
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String name = user.getDisplayName(); // Gets the actual user's name
        return ResponseEntity.ok(name);
    }

}
