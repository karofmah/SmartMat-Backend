package idatt2106v231.backend.service;

import idatt2106v231.backend.auth.AuthenticationResponse;
import idatt2106v231.backend.config.JwtService;
import idatt2106v231.backend.dto.user.UserAuthenticationDto;
import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.model.Refrigerator;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.model.User;
import lombok.RequiredArgsConstructor;
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
    private final RefrigeratorRepository refrigeratorRepository;

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
        if(repository.findById(request.getEmail()).isPresent()) {
            return null;
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .age(request.getAge())
                .household(request.getHousehold())
                .role(Role.USER)
                .build();
        repository.save(user);

        var ref = Refrigerator.builder()
                .user(user)
                .build();
        refrigeratorRepository.save(ref);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public boolean emailIsUsed(String email) {
        return repository.findByEmail(email).isPresent();
    }
    /**
     * Authenticate a user, used in login.
     *
     * @param request email and password for the user
     * @return a token which authenticates the user if the password is correct
     */
    public AuthenticationResponse authenticate(UserAuthenticationDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var optionalUser = repository.findByEmail(request.getEmail());
        User user = optionalUser.get();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
