package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("http://localhost:8000/")
public class UserController {

    private final Logger logger;

    private final UserServices userServices;

    @Autowired
    public UserController(UserServices userServices) {
        this.userServices = userServices;
        this.logger = LoggerFactory.getLogger(UserController.class);
    }

    @GetMapping("/login/getUser")
    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve user")
    })
    public ResponseEntity<Object> getUser(@RequestParam String email){
        ResponseEntity<Object> response;
        if (userServices.userNotExists(email)){
           response = new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
           logger.info((String)response.getBody());
           return response;
        }

        UserDto user = userServices.getUser(email);
        if(user == null){
            response = new ResponseEntity<>("User not retrieved", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
            return response;
        }

        logger.info("User is retrieved");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/updateUser")
    @Operation(summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is updated"),
            @ApiResponse(responseCode = "404", description = "User does not exist"),
            @ApiResponse(responseCode = "500", description = "User could not be update")
    })
    public ResponseEntity<Object> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        ResponseEntity<Object> response;

        if (userServices.userNotExists(userUpdateDto.getEmail())){
            response = new ResponseEntity<>("User does not exists", HttpStatus.NOT_FOUND);
        }
        else if(userServices.updateUser(userUpdateDto)){
            response = new ResponseEntity<>("User is updated", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("User is not updated", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info((String) response.getBody());
        return response;
    }
}