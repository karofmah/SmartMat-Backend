package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.RefrigeratorDto;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.service.RefrigeratorServices;
import idatt2106v231.backend.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/refrigerators")
@Tag(name = "Refrigerator API", description = "API for managing refrigerators")
public class RefrigeratorController {
    private final Logger logger =
            LoggerFactory.getLogger(ItemController.class);

    private RefrigeratorServices refrigeratorServices;

    private UserServices userServices;

    @Autowired
    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    @Autowired
    public void setRefrigeratorServices(RefrigeratorServices refrigeratorServices) {
        this.refrigeratorServices = refrigeratorServices;
    }

    @PostMapping("/saveRefrigerator")
    @Operation(summary = "Save new refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refrigerator is saved to database"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist in database"),
            @ApiResponse(responseCode = "500", description = "Failed to save refrigerator"),
    })
    public ResponseEntity<Object> saveRefrigerator(@RequestBody RefrigeratorDto refrigerator) {
        ResponseEntity<Object> response;
        if (refrigeratorServices.refrigeratorExists(refrigerator.getUserEmail())) {
            response= new ResponseEntity<>("Refrigerator already exists with this email", HttpStatus.IM_USED);
        }
        else if (refrigeratorServices.saveRefrigerator(refrigerator)) {
            response = new ResponseEntity<>("Refrigerator is saved to database", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Failed to save refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info((String) response.getBody());
        return response;
    }


    @DeleteMapping("/deleteRefrigerator")
    @Operation(summary = "Delete Refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refrigerator is removed from database"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist in database")
    })
    public ResponseEntity<Object> deleteRefrigerator(@RequestParam Integer registerId) {
        ResponseEntity<Object> response;
        if (refrigeratorServices.deleteRefrigerator(registerId)) {
            response = new ResponseEntity<>("Refrigerator removed from database", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
        }
        logger.info(response.getBody() + "");
        return response;
    }
}
