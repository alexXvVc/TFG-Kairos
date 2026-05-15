package com.parish.celebrations.auth;

import com.parish.celebrations.auth.dto.LoginRequest;
import com.parish.celebrations.auth.dto.LoginResponse;
import com.parish.celebrations.security.JwtProperties;
import com.parish.celebrations.security.JwtService;
import com.parish.celebrations.user.domain.AppUser;
import com.parish.celebrations.user.domain.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProps;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder,
                          JwtService jwtService, JwtProperties jwtProps) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.jwtProps = jwtProps;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        AppUser user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generate(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, jwtProps.getExpirationMs(), user.getRole().name());
    }
}
