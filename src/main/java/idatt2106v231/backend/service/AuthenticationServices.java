package idatt2106v231.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.fullstackproject.auth.AuthenticationResponse;
import ntnu.idatt2105.fullstackproject.config.JwtService;
import ntnu.idatt2105.fullstackproject.dto.user.UserCreationDto;
import ntnu.idatt2105.fullstackproject.model.Role;
import ntnu.idatt2105.fullstackproject.model.User;
import ntnu.idatt2105.fullstackproject.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The type Authentication service.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServices {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a user.
     *
     * @param request email and password for the user
     * @return a token which authenticates the user
     */
    public AuthenticationResponse register(UserCreationDto request) {
        if(repository.findDistinctByEmail(request.getEmail()) != null) {
            return null;
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticate a user, used in login.
     *
     * @param request email and password for the user
     * @return a token which authenticates the user if the password is correct
     */
    public AuthenticationResponse authenticate(UserCreationDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findDistinctByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
