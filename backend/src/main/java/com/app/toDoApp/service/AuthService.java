package com.app.toDoApp.service;

import com.app.toDoApp.dto.auth.JwtResponse;
import com.app.toDoApp.dto.auth.LoginRequest;
import com.app.toDoApp.dto.auth.RegisterRequest;
import com.app.toDoApp.entity.User;
import com.app.toDoApp.exceptions.EmailAlreadyExistsException;
import com.app.toDoApp.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;



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

    @Transactional
    public JwtResponse registerUser(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration attempt with existing email: {}", request.getEmail());
            throw new EmailAlreadyExistsException("The email is already registered");
        }

        // Create and save user
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt the password

        User savedUser = userRepository.save(user);
        logger.info("New user registered with email: {}", savedUser.getEmail());

        // Create UserPrincial to generate token
        UserPrincipal userPrincipal = UserPrincipal.create(savedUser);

        // Generate the kwt
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


