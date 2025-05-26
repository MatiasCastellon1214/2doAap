package com.app.toDoApp.controller;

import com.app.toDoApp.dto.auth.JwtResponse;
import com.app.toDoApp.dto.auth.LoginRequest;
import com.app.toDoApp.dto.entrada.UserEntradaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;
import com.app.toDoApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }


    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody UserEntradaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

}
