package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.RefrigeratorDto;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.service.RefrigeratorServices;
import idatt2106v231.backend.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/refrigerators")
@Tag(name = "Refrigerator API", description = "API for managing refrigerators")
public class RefrigeratorController {

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
    public ResponseEntity<Object> saveRefrigerator(@RequestBody RefrigeratorDto refrigerator) {
        if (refrigeratorServices.checkIfRefrigeratorExists(refrigerator.getUser())){
            return new ResponseEntity<>("Refrigerator already exists", HttpStatus.IM_USED);
        }

        ResponseEntity<Object> response = validateRefrigeratorDto(refrigerator);

        if (response.getStatusCode().equals(HttpStatus.OK)){
            if (refrigeratorServices.saveRefrigerator(refrigerator)){
                response = new ResponseEntity<>("Refrigerator is saved to database", HttpStatus.OK);
            }else{
                response =  new ResponseEntity<>("Data is not valid", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    private ResponseEntity<Object> validateRefrigeratorDto(RefrigeratorDto refrigeratorDto){
        if (refrigeratorDto.getUser().getEmail().isEmpty()
                || refrigeratorDto.getUser().getAge()==0
                || refrigeratorDto.getUser().getFirstName().isEmpty()
                || refrigeratorDto.getUser().getLastName().isEmpty()
                || refrigeratorDto.getUser().getHousehold()==0
                || refrigeratorDto.getUser().getPhoneNumber()==0){
            return new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        if (!userServices.checkIfUserExists(refrigeratorDto.getUser())){
            return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
