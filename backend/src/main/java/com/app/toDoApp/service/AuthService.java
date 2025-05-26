package com.app.toDoApp.service;

import com.app.toDoApp.dto.auth.JwtResponse;
import com.app.toDoApp.dto.auth.LoginRequest;
import com.app.toDoApp.dto.entrada.UserEntradaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;
import com.app.toDoApp.security.UserPrincipal;
import com.app.toDoApp.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final IUserService userService;

    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(userPrincipal);

        return new JwtResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getFirstName(),
                userPrincipal.getLastName()
        );
    }

    public JwtResponse registerUser(UserEntradaDTO request) {
        userService.createUser(request);
        LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
        return authenticateUser(loginRequest);
    }




}
