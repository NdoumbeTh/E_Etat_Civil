package com.epfafrica.etatcivil.controller;

import com.epfafrica.etatcivil.dto.LoginRequest;
import com.epfafrica.etatcivil.dto.LoginResponse;
import com.epfafrica.etatcivil.dto.RegisterRequest;
import com.epfafrica.etatcivil.exception.EmailDejaUtiliseException;
import com.epfafrica.etatcivil.exception.IdentifiantsInvalidesException;
import com.epfafrica.etatcivil.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (EmailDejaUtiliseException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IdentifiantsInvalidesException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
