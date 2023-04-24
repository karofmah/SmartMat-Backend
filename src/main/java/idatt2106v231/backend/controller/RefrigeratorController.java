package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.service.ItemServices;
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

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/refrigerators")
@Tag(name = "Refrigerator API", description = "API for managing refrigerators")
public class RefrigeratorController {

    private final Logger logger = LoggerFactory.getLogger(RefrigeratorController.class);

    private RefrigeratorServices refrigeratorServices;

    private ItemServices itemServices;

    @Autowired
    public void setRefrigeratorServices(RefrigeratorServices refrigeratorServices) {
        this.refrigeratorServices = refrigeratorServices;
    }
    @Autowired
    public void setItemServices(ItemServices itemServices) {
        this.itemServices = itemServices;
    }

    @GetMapping("/getRefrigerator/{refrigeratorId}")
    @Operation(summary = "Get refrigerator by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getRefrigeratorById(@PathVariable("refrigeratorId") Integer refrigeratorId) {
        if (!refrigeratorServices.refrigeratorExists(refrigeratorId)){
            ResponseEntity<Object> response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }
        return getRefrigerator(refrigeratorServices.getRefrigeratorById(refrigeratorId));
    }

    @GetMapping("/getRefrigerator/{userEmail}")
    @Operation(summary = "Get refrigerator by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getRefrigeratorByUser(@PathVariable("userEmail") String userEmail) {
        if (!refrigeratorServices.refrigeratorExists(userEmail)){
            ResponseEntity<Object> response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }
        return getRefrigerator(refrigeratorServices.getRefrigeratorByUserEmail(userEmail));
    }

    private ResponseEntity<Object> getRefrigerator(RefrigeratorDto refrigerator){
        ResponseEntity<Object> response;

        if (refrigerator == null){
            response = new ResponseEntity<>("Failed to retrieve refrigerator", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        }
        else {
            response = new ResponseEntity<>(refrigerator, HttpStatus.OK);
            logger.info("Refrigerator retrieved");
        }
        return response;
    }

    @GetMapping("/getAllRefrigerators")
    @Operation(summary = "Get all refrigerators ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return refrigerators"),
            @ApiResponse(responseCode = "404", description = "Refrigerators not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getAllRefrigerators() {
        ResponseEntity<Object> response;
        List<RefrigeratorDto> refrigerators = refrigeratorServices.getAllRefrigerators();
        if (refrigerators == null){
            response = new ResponseEntity<>("Failed to retrieve refrigerators", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info(response.getBody() + "");
        }
        else if (refrigerators.isEmpty()){
            response = new ResponseEntity<>("There are no refrigerators registered in the database", HttpStatus.NO_CONTENT);
            logger.info(response.getBody() + "");
        }
        else {
            response = new ResponseEntity<>(refrigerators, HttpStatus.OK);
            logger.info("Items retrieved");
        }
        return response;
    }

    @GetMapping("/getItemsInRefrigerator/{refrigeratorId}")
    @Operation(summary = "Get items in refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return items in refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator does not exist"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve items in refrigerator")
    })
    public ResponseEntity<Object> getItemsInRefrigerator(@PathVariable("refrigeratorId") Integer refrigeratorId) {
        ResponseEntity<Object> response;

        if (!refrigeratorServices.refrigeratorExists(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
            logger.info(response.getBody() + "");
            return response;
        }

        List<ItemDto> items = refrigeratorServices.getItemsInRefrigerator(refrigeratorId);
        if (items == null){
            response = new ResponseEntity<>("Failed to retrieve items in refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info(response.getBody() + "");
        }
        else if (items.isEmpty()){
            response = new ResponseEntity<>("There are no refrigerators registered in the database", HttpStatus.NO_CONTENT);
            logger.info(response.getBody() + "");
        }
        else {
            response = new ResponseEntity<>(items, HttpStatus.OK);
            logger.info("Items retrieved");
        }
        return response;
    }

    @PostMapping("/addItemInRefrigerator")
    @Operation(summary = "Add item in refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item is added to refrigerator"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "404", description = "Refrigerator or/and item does not exist"),
            @ApiResponse(responseCode = "500", description = "Item is not added to refrigerator")
    })
    public ResponseEntity<Object> addItemInRefrigerator(@RequestBody ItemInRefrigeratorDto dto){
        ResponseEntity<Object> response = validateItemInRefrigerator(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }

        if (itemServices.itemExistInRefrigerator(dto.getItemName(), dto.getRefrigeratorId()) &&
                refrigeratorServices.updateItemInRefrigerator(dto) ) {
            response = new ResponseEntity<>("Item is updated", HttpStatus.OK);
        }
        else if(refrigeratorServices.addItemToRefrigerator(dto)){
            response = new ResponseEntity<>("Item is added to refrigerator", HttpStatus.OK);
        }
        else {
            response = new ResponseEntity<>("Item is not added to refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
           }
        logger.info((String) response.getBody());
        return response;
    }

    @DeleteMapping("/removeItemFromRefrigerator")
    @Operation(summary = "Remove items in refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Removed item from refrigerator"),
            @ApiResponse(responseCode = "400", description = "Data is not valid"),
            @ApiResponse(responseCode = "404", description = "Refrigerator or/and item does not exist"),
            @ApiResponse(responseCode = "500", description = "Item is not removed from refrigerator")
    })
    public ResponseEntity<Object> removeItemFromRefrigerator(@RequestBody ItemInRefrigeratorDto dto){
        ResponseEntity<Object> response = validateItemInRefrigerator(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }
        System.out.println(dto);

        if (!itemServices.itemExistInRefrigerator(dto.getItemName(), dto.getRefrigeratorId()) ) {
            response = new ResponseEntity<>("Item does not exist in refrigerator", HttpStatus.NOT_FOUND);
        }
        else if(refrigeratorServices.deleteItemFromRefrigerator(dto)){
            response = new ResponseEntity<>("Item is removed from refrigerator", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Item is not removed from refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        logger.info((String) response.getBody());
        return response;
    }

    public ResponseEntity<Object> validateItemInRefrigerator(ItemInRefrigeratorDto dto){
        ResponseEntity<Object> response;
        if (dto.getRefrigeratorId() == -1 || dto.getItemName().isEmpty() || dto.getAmount() == 0){
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if(!refrigeratorServices.refrigeratorExists(dto.getRefrigeratorId())){
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        }
        else if(!itemServices.checkIfItemExists(dto.getItemName())){
            response = new ResponseEntity<>("Item does not exist", HttpStatus.NOT_FOUND);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }

}
