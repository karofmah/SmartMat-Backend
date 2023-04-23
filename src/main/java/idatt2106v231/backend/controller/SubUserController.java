package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.service.SubUserServices;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/subusers")
@CrossOrigin("http://localhost:8000/")
public class SubUserController {

    private static final Logger logger = LoggerFactory.getLogger(SubUserServices.class);

    @Autowired
    private SubUserServices subUserServices;

    @Autowired
    private UserServices userServices;

    @GetMapping("/getUsersFromMaster")
    @Operation(summary = "Get subusers from master")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved subusers from database"),
            @ApiResponse(responseCode = "400", description = "Masteruser does not exist in database")
    })
    public ResponseEntity<Object> getUsersFromMaster(@RequestParam String email) {
        ResponseEntity<Object> response;
        if(!userServices.userExists(email)) {
            response = new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        } else {
            response = new ResponseEntity<>(subUserServices.getSubUsersByMaster(email), HttpStatus.OK);
            logger.info("Retrieving subusers with master " + email);
        }
        return response;
    }

    @GetMapping("/getUserByNameAndMaster")
    @Operation(summary = "Get subuser from master and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved subuser from database"),
            @ApiResponse(responseCode = "400", description = "Items does not exist in database")
    })
    public ResponseEntity<Object> getUserByMasterAndName(@RequestParam String email, String name) {
        ResponseEntity<Object> response;
        if(!userServices.userExists(email)) {
            response = new ResponseEntity<>("Masteruser not found", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        } else if(!subUserServices.subUserExists(name, email)) {
            response = new ResponseEntity<>("Subuser not found", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        } else {
            response = new ResponseEntity<>(subUserServices.getSubUserByMasterAndName(email, name), HttpStatus.OK);
            logger.info("Retrieving subuser with name " + name + " and masteruser " + email);
        }
        return response;
    }

    @PostMapping("/addSubUser")
    @Operation(summary = "Add new subuser to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subuser added database"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid"),
            @ApiResponse(responseCode = "226", description = "Masteruser already has subuser with that name")
    })
    public ResponseEntity<Object> addSubUser(@RequestBody SubUserDto subUser) {
        ResponseEntity<Object> response;
        if(subUserServices.subUserExists(subUser.getName(), subUser.getMasterUser())) {
            response = new ResponseEntity<>("Subuser already exists", HttpStatus.IM_USED);
        } else if (!userServices.userExists(subUser.getMasterUser())){
            response = new ResponseEntity<>("Masteruser doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(subUser.getMasterUser() == null) {
            response = new ResponseEntity<>("Masteruser is not defined", HttpStatus.BAD_REQUEST);
        } else if(subUser.getName() == null) {
            response = new ResponseEntity<>("Subuser name is not defined", HttpStatus.BAD_REQUEST);
        } else if(subUser.getAccessLevel() == null) {
            response = new ResponseEntity<>("Accesslevel is not defined", HttpStatus.BAD_REQUEST);
        } else {
            subUserServices.saveSubUser(subUser);
            response = new ResponseEntity<>("Subuser saved successfully", HttpStatus.OK);
        }
        logger.info(response.getBody() + "");
        return response;
    }

    @DeleteMapping("/deleteSubUser")
    @Operation(summary = "Delete a subuser from a masteruser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subuser deleted"),
            @ApiResponse(responseCode = "400", description = "Subuser doesnt exist"),
    })
    public ResponseEntity<Object> deleteSubUser(@RequestBody SubUserDto subUser) {
        ResponseEntity<Object> response;
        if(!subUserServices.subUserExists(subUser.getName(), subUser.getMasterUser())) {
            response = new ResponseEntity<>("Subuser doesnt exist", HttpStatus.BAD_REQUEST);
        } else {
            subUserServices.deleteSubUser(subUser);
            response = new ResponseEntity<>("Subuser deleted", HttpStatus.OK);
        }
        logger.info(response.getBody() + "");
        return response;
    }
}
