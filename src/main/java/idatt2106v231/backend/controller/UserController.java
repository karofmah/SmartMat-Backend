
package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.user.UserCreationDto;
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
@CrossOrigin("*")
public class UserController {

    private UserServices userServices;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public void setUserService(UserServices userServices) {
        this.userServices=userServices;
    }

    @GetMapping("/login/getUser")
    public ResponseEntity<Object> getUser(@RequestParam String email){
        try{
            UserCreationDto user=userServices.getUser(email);

            if(user==null){
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PutMapping("/updateUser")
    @Operation(summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is updated"),
            @ApiResponse(responseCode = "404", description = "User does not exist"),
            @ApiResponse(responseCode = "500", description = "User could not be update")
    })
    public ResponseEntity<Object> updateUser(@RequestParam String email, @RequestBody UserUpdateDto userUpdateDto) {

        ResponseEntity<Object> response;

        if (!userServices.checkIfUserExists(email)){
            response = new ResponseEntity<>("User does not exists", HttpStatus.NOT_FOUND);
            logger.info(String.valueOf(response.getBody()));
            return response;
        } else if(userServices.updateUser(email, userUpdateDto)){
            response = new ResponseEntity<>("User is updated", HttpStatus.OK);
        } else{
            response = new ResponseEntity<>("User could not be updated", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}