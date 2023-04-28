package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.subuser.SubUserDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/subusers")
@CrossOrigin("http://localhost:8000/")
public class SubUserController {

    private static final Logger logger = LoggerFactory.getLogger(SubUserServices.class);

    private SubUserServices subUserServices;
    private UserServices userServices;

    @Autowired
    public void setSubUserServices(SubUserServices subUserServices) {
        this.subUserServices = subUserServices;
    }

    @Autowired
    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/getUsersFromMaster")
    @Operation(summary = "Get subusers from master")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved subusers from database"),
            @ApiResponse(responseCode = "400", description = "Masteruser does not exist in database"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve subusers")
    })
    public ResponseEntity<Object> getUsersFromMaster(@RequestParam String email) {
        ResponseEntity<Object> response;
        if(!userServices.checkIfUserExists(email)) {
            response = new ResponseEntity<>("Master user not found", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }
        List<SubUserDto> subUsers = subUserServices.getSubUsersByMaster(email);

        if (subUsers == null){
            response = new ResponseEntity<>("Failed to retrieve subusers", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info(response.getBody() + "");
        }
        else {
            response = new ResponseEntity<>(subUsers, HttpStatus.OK);
            logger.info("Retrieving subusers with master " + email);
        }
        return response;
    }

    @GetMapping("/getUser/{subUserId}")
    @Operation(summary = "Get subuser from master and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved subuser from database"),
            @ApiResponse(responseCode = "404", description = "Subuser not found in database"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve the subuser")
    })
    public ResponseEntity<Object> getUserByMasterAndName(@PathVariable("subUserId") int subUserId) {
        ResponseEntity<Object> response;

        if (!subUserServices.subUserExists(subUserId)) {
            response = new ResponseEntity<>("Subuser not found", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }

        SubUserDto subUser = subUserServices.getSubUser(subUserId);
        if(subUser == null){
            response = new ResponseEntity<>("Failed to retrieve subuser", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info(response.getBody() + "");
        }
        else {
            response = new ResponseEntity<>(subUser, HttpStatus.OK);
            logger.info("Retrieving subuser");
        }
        return response;
    }

    @PostMapping("/addSubUser")
    @Operation(summary = "Add new subuser to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subuser added database"),
            @ApiResponse(responseCode = "226", description = "Masteruser already has subuser with that name"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "500", description = "Failed to save subuser")

    })
    public ResponseEntity<Object> addSubUser(@RequestBody SubUserDto subDto) {
        ResponseEntity<Object> response = validateDto(subDto);

        if(response.getStatusCode() != HttpStatus.OK) {
            logger.info(response.getBody() + "");
            return response;
        }
        if (subUserServices.subUserExists(subDto.getName(), subDto.getUserEmail())) {
            response = new ResponseEntity<>("Subuser already exists", HttpStatus.IM_USED);
        }
        else if (subUserServices.saveSubUser(subDto)){
            response = new ResponseEntity<>("Subuser saved successfully", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Failed to save subuser", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info(response.getBody() + "");
        return response;
    }

    @DeleteMapping("/deleteSubUser/{subUserId}")
    @Operation(summary = "Delete a subuser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subuser deleted"),
            @ApiResponse(responseCode = "400", description = "Subuser doesnt exist"),
            @ApiResponse(responseCode = "500", description = "Failed to delete subuser")
    })
    public ResponseEntity<Object> deleteSubUser(@PathVariable("subUserId") int subUserId) {
        ResponseEntity<Object> response;

        if (!subUserServices.subUserExists(subUserId)) {
            response = new ResponseEntity<>("Subuser not found", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }

        if (subUserServices.deleteSubUser(subUserId)){
            response = new ResponseEntity<>("Subuser deleted", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Failed to delete subuser", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info(response.getBody() + "");
        return response;
    }


    @PostMapping("/updateSubuser")
    @Operation(summary = "Update a subuser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subuser is updated"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "404", description = "Subuser doesnt exist"),
            @ApiResponse(responseCode = "500", description = "Failed to delete subuser")
    })
    public ResponseEntity<Object> updateSubUser(@RequestBody SubUserDto subDto) {
        ResponseEntity<Object> response;

        if (!subUserServices.subUserExists(subDto.getSubUserId())) {
            response = new ResponseEntity<>("Subuser does not exists", HttpStatus.NOT_FOUND);
        }
        else if (subUserServices.updateSubUser(subDto)){
            response = new ResponseEntity<>("Subuser updated", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Failed to update subuser", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info(response.getBody() + "");
        return response;
    }

    private ResponseEntity<Object> validateDto(SubUserDto subDto) {
        ResponseEntity<Object> response;
        if (subDto.getUserEmail() == null || subDto.getUserEmail().isEmpty() ||
                subDto.getName() == null || subDto.getName().isEmpty()) {
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if (!userServices.checkIfUserExists(subDto.getUserEmail())) {
            response = new ResponseEntity<>("Masteruser not found", HttpStatus.NOT_FOUND);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }
}
