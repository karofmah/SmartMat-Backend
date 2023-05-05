package idatt2106v231.backend.service;

import idatt2106v231.backend.auth.AuthenticationResponse;
import idatt2106v231.backend.model.ShoppingList;
import idatt2106v231.backend.repository.ShoppingListRepository;
import idatt2106v231.backend.security.JwtService;
import idatt2106v231.backend.dto.user.UserAuthenticationDto;
import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.model.Refrigerator;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.WeekMenuRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Class to manage user authentication.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServices {

    private UserRepository userRepo;
    private RefrigeratorRepository refRepo;
    private WeekMenuRepository weekMenuRepo;
    private ShoppingListRepository shoppingListRepo;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor which sets the repositories to use for database access.
     */
    @Autowired
    public AuthenticationServices(UserRepository userRepo, RefrigeratorRepository refRepo, WeekMenuRepository weekMenuRepo,
                                  ShoppingListRepository shoppingListRepo, PasswordEncoder passwordEncoder,
                                  JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.refRepo = refRepo;
        this.weekMenuRepo = weekMenuRepo;
        this.shoppingListRepo = shoppingListRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Method to register a user.
     *
     * @param request email and password for the user
     * @return a token which authenticates the user
     */
    public AuthenticationResponse register(UserCreationDto request) {
        if(emailIsUsed(request.getEmail())) {
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
        userRepo.save(user);

        var ref = Refrigerator.builder()
                .user(user)
                .build();
        refRepo.save(ref);

        var shoppingList = ShoppingList.builder()
                .user(user)
                .build();
        shoppingListRepo.save(shoppingList);

        var weekMenu = WeeklyMenu.builder()
                .user(user)
                .build();
        weekMenuRepo.save(weekMenu);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Method to check if an email is already in use.
     *
     * @param email the email to check
     * @return true if the email is in use
     */
    public boolean emailIsUsed(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    /**
     * Method to authenticate a user, used in login.
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

        User user = userRepo.findByEmail(request.getEmail()).get();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}