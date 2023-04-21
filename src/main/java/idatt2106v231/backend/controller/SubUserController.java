package idatt2106v231.backend.controller;

import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.service.SubUserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/subusers")
@CrossOrigin("http://localhost:8000/")
public class SubUserController {

    private SubUserServices subUserServices;

    @GetMapping("/getusersfrommaster")
    public ResponseEntity<Object> getUsersFromMaster(@RequestParam String email) {
        //TODO sjekke om master finnes
    }
}
