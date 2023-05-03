package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.GarbageServices;
import idatt2106v231.backend.service.RefrigeratorServices;
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
@RequestMapping("/api/garbages")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Garbage API", description = "API for managing garbages")
public class GarbageController {

    private final Logger logger = LoggerFactory.getLogger(GarbageController.class);

    private GarbageServices garbageServices;

    private RefrigeratorServices refrigeratorServices;

    @Autowired
    public void setRefrigeratorServices(RefrigeratorServices refrigeratorServices) {
        this.refrigeratorServices = refrigeratorServices;
    }

    @Autowired
    public void setGarbageServices(GarbageServices garbageServices) {
        this.garbageServices = garbageServices;
    }

    @GetMapping("/refrigerator/totalAmountYear/{refrigeratorId}")
    @Operation(summary = "Calculate total amount of garbage from a refrigerator in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated total amount"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified"),

    })
    public ResponseEntity<Object> calculateTotalAmountByIdYear(@PathVariable int refrigeratorId,@RequestParam int year) {


        ResponseEntity <Object> response=validateGarbageYearDto(refrigeratorId,year);
        ResponseEntity <Object> response=validateGarbageYearDto(garbageYearDto);

        if(response.getStatusCode().equals(HttpStatus.OK)){
            if (garbageServices.calculateTotalAmount(refrigeratorId,year)!=-1){
                response = new ResponseEntity<>(garbageServices.calculateTotalAmount(refrigeratorId, year), HttpStatus.OK);
            }
            else {
                response = new ResponseEntity<>("Total amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info(String.valueOf(response.getBody()));
        return response;
    }
    public ResponseEntity<Object> validateGarbageYearDto(int refrigeratorId,int year){
    @GetMapping("/refrigerator/amountEachMonth/{refrigeratorId}")
    @Operation(summary = "Calculate amount of garbage each month in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated amount each month"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified"),

    })
    public ResponseEntity<Object> calculateAmountEachMonth(@PathVariable int refrigeratorId,@RequestParam int year) {

        ResponseEntity <Object> response=validateGarbageYearDto(refrigeratorId,year);

        if(response.getStatusCode().equals(HttpStatus.OK)){
            if (garbageServices.calculateAmountEachMonth(refrigeratorId, year)!=null) {
                response = new ResponseEntity<>(
                        garbageServices.calculateAmountEachMonth(refrigeratorId, year),
                        HttpStatus.OK);
            }else{
                response = new ResponseEntity<>("Could not calculate amount of garbage each month",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info(String.valueOf(response.getBody()));
        return response;
    }
    public ResponseEntity<Object> validateGarbageYearDto(int refrigeratorId,int year){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if(refrigeratorId<=0 || year<=0){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (!refrigeratorServices.refrigeratorExists(refrigeratorId)) {
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        } else if (!garbageServices.refrigeratorHasGarbages(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not have garbages", HttpStatus.NOT_FOUND);

        }
        return response;
    }
    public ResponseEntity<Object> validateGarbageYearDto(GarbageYearDto garbageYearDto){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if(refrigeratorId<=0 || year<=0){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (!refrigeratorServices.refrigeratorExists(refrigeratorId)) {
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        } else if (garbageServices.refrigeratorIsEmpty(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not have garbages", HttpStatus.NOT_FOUND);

        }
        return response;
    }

}

