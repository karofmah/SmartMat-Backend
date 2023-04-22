package idatt2106v231.backend.controller;

import idatt2106v231.backend.auth.AuthenticationResponse;
import idatt2106v231.backend.dto.user.UserAuthenticationDto;
import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.service.AuthenticationServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServices service;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    @Operation(summary = "Regsiter a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "226", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "One or more fields are missing")
    })
    public ResponseEntity<Object> register(
            @RequestBody UserCreationDto request
    ) {
        if(request.getEmail() == null ||
                request.getPassword() == null ||
                request.getFirstName() == null ||
                request.getLastName() == null ||
                request.getAge() == 0 ||
                request.getPhoneNumber() == 0 ||
                request.getHousehold() == 0) {
            logger.info("One or more fields are missing");
            return new ResponseEntity<>("One or more fields are missing", HttpStatus.BAD_REQUEST);
        }
        if(service.emailIsUsed(request.getEmail())) {
            logger.info("Email is already in use");
            return new ResponseEntity<>("User already exists", HttpStatus.IM_USED);
        }

        AuthenticationResponse response = service.register(request);

        logger.info("Creating user with token");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Log in a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated"),
            @ApiResponse(responseCode = "403", description = "Wrong username or password")
    })
    public ResponseEntity<Object> authenticate(
            @RequestBody UserAuthenticationDto request
    ) {
        logger.info("Attempting to log in with user " + request.getEmail());
        AuthenticationResponse response = service.authenticate(request);

        if(response.getToken() == null) {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
