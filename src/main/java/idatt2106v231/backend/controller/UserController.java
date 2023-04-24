
package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    private UserServices userServices;
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

}