package idatt2106v231.backend.controller;

import idatt2106v231.backend.model.User;
import idatt2106v231.backend.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("http://localhost:8000/")
public class UserController {

    private UserServices userServices;
    @Autowired
    public void setUserService(UserServices userServices) {
        this.userServices=userServices;
    }

    @GetMapping("/login/user")
    public ResponseEntity<Optional<User>> getUser(@RequestParam String email){
        try{
            Optional <User> user=userServices.getUser(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
