package com.pokeapp.pokeapp.controller;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password"))
            );
            String token = jwtUtils.generateToken(body.get("username"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        if (userRepository.existsByUsername(body.get("username"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "El username ya existe"));
        }
        if (userRepository.existsByEmail(body.get("email"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email ya existe"));
        }
        User user = new User();
        user.setUsername(body.get("username"));
        user.setEmail(body.get("email"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
    }
}
