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

    private final Logger logger;

    private final GarbageServices garbageServices;

    private final RefrigeratorServices refrigeratorServices;

    @Autowired
    public GarbageController(GarbageServices garbageServices, RefrigeratorServices refrigeratorServices) {
        this.garbageServices = garbageServices;
        this.refrigeratorServices = refrigeratorServices;
        this.logger = LoggerFactory.getLogger(GarbageController.class);
    }

    @GetMapping("/refrigerator/totalAmountYear/{refrigeratorId}")
    @Operation(summary = "Calculate total amount of garbage from a refrigerator in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated total amount"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified")
    })
    public ResponseEntity<Object> calculateTotalAmountByYearAndRefrigerator(@PathVariable int refrigeratorId, @RequestParam int year) {
        ResponseEntity<Object> response = validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());
            return response;
        }

        double totalAmountByYear = garbageServices.calculateTotalAmountByYearAndRefrigerator(refrigeratorId, year);

        if (totalAmountByYear != -1){
            response = new ResponseEntity<>(totalAmountByYear, HttpStatus.OK);
            logger.info("Calculated amount of garbage");
        }
        else {
            response = new ResponseEntity<>("Total amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }

        return response;
    }

    @GetMapping("/refrigerator/amountEachMonth/{refrigeratorId}")
    @Operation(summary = "Calculate amount of garbage each month in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated amount each month"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified")
    })
    public ResponseEntity<Object> calculateAmountEachMonthByYearAndRefrigerator(@PathVariable int refrigeratorId, @RequestParam int year) {
        ResponseEntity <Object> response = validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());
            return response;
        }

        if (garbageServices.calculateTotalAmountEachMonthByYearAndRefrigerator(refrigeratorId, year) != null) {
            response = new ResponseEntity<>(garbageServices.calculateTotalAmountEachMonthByYearAndRefrigerator(refrigeratorId, year), HttpStatus.OK);
            logger.info("Calculated amount of garbage");
        }
        else {
            response = new ResponseEntity<>("Could not calculate amount of garbage each month",HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }

        return response;
    }

    @GetMapping("/averageAmountYear/{refrigeratorId}")
    @Operation(summary = "Calculate amount of garbage each month in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated amount each month"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified")
    })
    public ResponseEntity<Object> calculateAverageAmountByYearWithoutRefrigerator(@PathVariable int refrigeratorId, @RequestParam int year) {
        ResponseEntity <Object> response;

        if(year <= 0){
            response =  new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if (garbageServices.calculateAverageAmountByYearWithoutRefrigerator(refrigeratorId, year) != -1) {
            response = new ResponseEntity<>(garbageServices.calculateAverageAmountByYearWithoutRefrigerator(refrigeratorId, year), HttpStatus.OK);
            logger.info("Calculated amount of garbage");
        }
        else {
            response = new ResponseEntity<>("Average amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }

        return response;
    }

    @GetMapping("/averageAmountEachMonth/{refrigeratorId}")
    @Operation(summary = "Calculate amount of garbage each month in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated amount each month"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not have garbages"),
            @ApiResponse(responseCode = "500", description = "Amount can not be calculated"),
            @ApiResponse(responseCode = "400", description = "Data is not specified")
    })
    public ResponseEntity<Object> calculateAverageEachMonthByYearWithoutRefrigerator(@PathVariable int refrigeratorId, @RequestParam int year) {
        ResponseEntity <Object> response;

        if(year <= 0){
            response =  new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }

        else if (garbageServices.calculateAverageAmountEachMonthByYearAndRefrigerator(refrigeratorId, year) != null) {
            response = new ResponseEntity<>(garbageServices.calculateAverageAmountEachMonthByYearAndRefrigerator(refrigeratorId, year), HttpStatus.OK);
            logger.info("Calculated amount of garbage");
        }
        else {
            response = new ResponseEntity<>("Average amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }

        return response;
    }

    /**
     * Method to validate the input
     *
     * @param refrigeratorId the id of the refrigerator
     * @param year the year
     * @return Different HTTP status messages based on the validity of the data
     */
    public ResponseEntity<Object> validateGarbage(int refrigeratorId, int year){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if(refrigeratorId <= 0 || year <= 0){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (refrigeratorServices.refrigeratorNotExists(refrigeratorId)) {
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        }
        else if (garbageServices.hasGarbageByYear(refrigeratorId, year)){
            response = new ResponseEntity<>("Refrigerator does not have garbage this year", HttpStatus.NOT_FOUND);
        }
        return response;
    }
}

