package com.app.toDoApp.service;

import com.app.toDoApp.dto.auth.JwtResponse;
import com.app.toDoApp.dto.auth.LoginRequest;
import com.app.toDoApp.security.UserPrincipal;
import com.app.toDoApp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


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
}