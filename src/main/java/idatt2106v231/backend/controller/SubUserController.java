package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
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

    private final Logger logger;

    private final SubUserServices subUserServices;
    private final UserServices userServices;

    @Autowired
    public SubUserController(SubUserServices subUserServices, UserServices userServices) {
        this.subUserServices = subUserServices;
        this.userServices = userServices;
        this.logger = LoggerFactory.getLogger(SubUserServices.class);
    }

    @GetMapping("/getUsersFromMaster")
    @Operation(summary = "Get sub users from master")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved sub users from database"),
            @ApiResponse(responseCode = "404", description = "Master user does not exist in database"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve sub users")
    })
    public ResponseEntity<Object> getUsersFromMaster(@RequestParam String email) {
        ResponseEntity<Object> response;
        if(userServices.userNotExists(email)) {
            response = new ResponseEntity<>("Master user not found", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }
        List<SubUserDto> subUsers = subUserServices.getSubUsersByMaster(email);

        if (subUsers == null){
            response = new ResponseEntity<>("Failed to retrieve sub users", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(subUsers, HttpStatus.OK);
            logger.info("Retrieving sub users with master " + email);
        }
        return response;
    }

    @GetMapping("/getUser/{subUserId}")
    @Operation(summary = "Get sub user from master and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved sub user from database"),
            @ApiResponse(responseCode = "404", description = "Sub user not found in database"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve the sub user")
    })
    public ResponseEntity<Object> getSubUser(@PathVariable("subUserId") int subUserId) {
        ResponseEntity<Object> response;

        if (subUserServices.subUserNotExists(subUserId)) {
            response = new ResponseEntity<>("Sub user not found", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        SubUserDto subUser = subUserServices.getSubUser(subUserId);
        if(subUser == null){
            response = new ResponseEntity<>("Failed to retrieve sub user", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(subUser, HttpStatus.OK);
            logger.info("Retrieving sub user");
        }
        return response;
    }

    @PostMapping("/addSubUser")
    @Operation(summary = "Add new sub user to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub user added database"),
            @ApiResponse(responseCode = "226", description = "Master user already has sub user with that name"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "500", description = "Failed to save sub user")

    })
    public ResponseEntity<Object> addSubUser(@RequestBody SubUserCreationDto subDto) {
        ResponseEntity<Object> response = validateDto(subDto);

        if(response.getStatusCode() != HttpStatus.OK) {
            logger.info((String)response.getBody());
            return response;
        }
        if (subUserServices.subUserExists(subDto.getUserEmail(), subDto.getName())) {
            response = new ResponseEntity<>("Sub user already exists", HttpStatus.IM_USED);
        }
        else if (subUserServices.saveSubUser(subDto)){
            response = new ResponseEntity<>("Sub user saved successfully", HttpStatus.CREATED);
        }
        else{
            response = new ResponseEntity<>("Failed to save sub user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info((String)response.getBody());
        return response;
    }

    @DeleteMapping("/deleteSubUser/{subUserId}")
    @Operation(summary = "Delete a sub user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub user deleted"),
            @ApiResponse(responseCode = "400", description = "Sub user doesnt exist"),
            @ApiResponse(responseCode = "500", description = "Failed to delete sub user")
    })
    public ResponseEntity<Object> deleteSubUser(@PathVariable("subUserId") int subUserId) {
        ResponseEntity<Object> response;

        if (subUserServices.subUserNotExists(subUserId)) {
            response = new ResponseEntity<>("Sub user not found", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        if (subUserServices.deleteSubUser(subUserId)){
            response = new ResponseEntity<>("Sub user deleted", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Failed to delete sub user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    @PostMapping("/validatePinCode")
    public ResponseEntity<Object> validatePinCode(@RequestBody SubUserValidationDto subUser){
        ResponseEntity<Object> response;

        if (subUserServices.subUserNotExists(subUser.getSubUserId())) {
            response = new ResponseEntity<>("Sub user not found", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        if(subUserServices.pinCodeValid(subUser)){
            response = new ResponseEntity<>("Pin code is correct", HttpStatus.OK);
        }else{
            response = new ResponseEntity<>("Pin code is incorrect", HttpStatus.NOT_FOUND);
        }
        logger.info((String)response.getBody());
        return response;
    }


    @PutMapping("/updateSubuser")
    @Operation(summary = "Update a sub user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sub user is updated"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "404", description = "Sub user doesnt exist"),
            @ApiResponse(responseCode = "500", description = "Failed to delete sub user")
    })
    public ResponseEntity<Object> updateSubUser(@RequestBody SubUserDto subDto) {
        ResponseEntity<Object> response;

        if (subUserServices.subUserNotExists(subDto.getSubUserId())) {
            response = new ResponseEntity<>("Sub user does not exists", HttpStatus.NOT_FOUND);
        }
        else if(subDto.getName().isEmpty()){
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if (subUserServices.updateSubUser(subDto)){
            response = new ResponseEntity<>("Sub user updated", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Failed to update sub user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }


    /**
     *
     * Method to validate the input
     * @param subDto Dto object containing data to be validated
     * @return Different HTTP status messages based on the validity of the data
     */
    private ResponseEntity<Object> validateDto(SubUserCreationDto subDto) {
        ResponseEntity<Object> response;
        if (subDto.getUserEmail() == null || subDto.getUserEmail().isEmpty() ||
                subDto.getName() == null || subDto.getName().isEmpty()) {
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if (userServices.userNotExists(subDto.getUserEmail())) {
            response = new ResponseEntity<>("Master user not found", HttpStatus.NOT_FOUND);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }
}
