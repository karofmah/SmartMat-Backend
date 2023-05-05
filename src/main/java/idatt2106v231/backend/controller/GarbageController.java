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
    public ResponseEntity<Object> calculateTotalAmountByIdYear(@PathVariable int refrigeratorId, @RequestParam int year) {

        ResponseEntity <Object> response=validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());

            return response;
        }

        if (garbageServices.calculateTotalAmount(refrigeratorId, year) != -1){
            response = new ResponseEntity<>(garbageServices.calculateTotalAmount(refrigeratorId, year), HttpStatus.OK);
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
    public ResponseEntity<Object> calculateAmountEachMonth(@PathVariable int refrigeratorId, @RequestParam int year) {

        ResponseEntity <Object> response=validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());

            return response;
        }

        if (garbageServices.calculateTotalAmountEachMonth(refrigeratorId, year) != null) {
            response = new ResponseEntity<>(
                    garbageServices.calculateTotalAmountEachMonth(refrigeratorId, year), HttpStatus.OK);

            logger.info("Calculated amount of garbage");
        } else {
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
    public ResponseEntity<Object> calculateAverageAmountYear(@PathVariable int refrigeratorId, @RequestParam int year) {

        ResponseEntity <Object> response=validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());

            return response;
        }

        if (garbageServices.calculateAverageAmount(refrigeratorId, year) != -1) {
            response = new ResponseEntity<>(
                    garbageServices.calculateAverageAmount(refrigeratorId, year), HttpStatus.OK);
            logger.info("Calculated amount of garbage");

        } else {
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
    public ResponseEntity<Object> calculateAverageEachMonth(@PathVariable int refrigeratorId, @RequestParam int year) {

        ResponseEntity <Object> response = validateGarbage(refrigeratorId, year);

        if(!response.getStatusCode().equals(HttpStatus.OK)) {
            logger.info((String)response.getBody());

            return response;
        }

        if (garbageServices.calculateAverageAmountEachMonth(refrigeratorId, year) != null) {
            response = new ResponseEntity<>(
                    garbageServices.calculateAverageAmountEachMonth(refrigeratorId, year), HttpStatus.OK);
            logger.info("Calculated amount of garbage");

        } else {
            response = new ResponseEntity<>("Average amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());

        }

        return response;
    }

    /**
     *
     * Method to validate the input
     * @param refrigeratorId Id of the refrigerator being sent in
     * @param year Year being sent in
     * @return Different HTTP status messages based on the validity of the data
     */
    public ResponseEntity<Object> validateGarbage(int refrigeratorId, int year){

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if(refrigeratorId <= 0 || year <= 0){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (!refrigeratorServices.refrigeratorExists(refrigeratorId)) {
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        }
        else if (garbageServices.refrigeratorIsEmpty(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not have garbage", HttpStatus.NOT_FOUND);

        }
        return response;
    }
}

