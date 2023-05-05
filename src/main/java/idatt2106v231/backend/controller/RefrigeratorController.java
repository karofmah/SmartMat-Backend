package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.refrigerator.*;
import idatt2106v231.backend.service.GarbageServices;
import idatt2106v231.backend.service.ItemServices;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:8000/")
@RequestMapping("/api/refrigerators")
@Tag(name = "Refrigerator API", description = "API for managing refrigerators")
public class RefrigeratorController {

    private final Logger logger;

    private final RefrigeratorServices refrigeratorServices;
    private final UserServices userServices;
    private final ItemServices itemServices;
    private final GarbageServices garbageServices;

    @Autowired
    public RefrigeratorController(RefrigeratorServices refrigeratorServices, UserServices userServices,
                                  ItemServices itemServices, GarbageServices garbageServices) {
        this.refrigeratorServices = refrigeratorServices;
        this.userServices = userServices;
        this.itemServices = itemServices;
        this.garbageServices = garbageServices;
        this.logger = LoggerFactory.getLogger(RefrigeratorController.class);
    }


    @GetMapping("/getRefrigeratorByUser")
    @Operation(summary = "Get refrigerator by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getRefrigeratorByUser(@RequestParam String userEmail) {
        ResponseEntity<Object> response;

        if (userServices.userNotExists(userEmail)){
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        RefrigeratorDto refrigerator = refrigeratorServices.getRefrigeratorByUserEmail(userEmail);
        if (refrigerator == null){
            response = new ResponseEntity<>("Failed to retrieve refrigerator", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(refrigerator, HttpStatus.OK);
            logger.info("Refrigerator retrieved");
        }
        return response;
    }

    @GetMapping("/getItemInRefrigeratorByCategory/{refrigeratorId}")
    @Operation(summary = "Get items refrigerator by refrigerator and category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the items in the refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getItemsInRefrigeratorByCategory(@PathVariable("refrigeratorId") int refrigeratorId, @RequestParam int categoryId) {
        ResponseEntity<Object> response;

        if (refrigeratorServices.refrigeratorNotExists(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        List<ItemInRefrigeratorDto> items = refrigeratorServices.getItemsInRefrigeratorByCategory(refrigeratorId, categoryId);

        if (items == null){
            response = new ResponseEntity<>("Failed to retrieve items in refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(items, HttpStatus.OK);
            logger.info("Refrigerator retrieved");
        }
        return response;
    }

    @GetMapping("/getItemInRefrigeratorByExpirationDate/{refrigeratorId}")
    @Operation(summary = "Get items refrigerator by refrigerator and expiration date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the items in the refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve refrigerator")
    })
    public ResponseEntity<Object> getItemsInRefrigeratorByExpirationDate(@PathVariable("refrigeratorId") int refrigeratorId) throws ParseException {
        ResponseEntity<Object> response;

        if (refrigeratorServices.refrigeratorNotExists(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not exists", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date start = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date end = calendar.getTime();

        List<ItemInRefrigeratorDto> items = refrigeratorServices
                .getItemsInRefrigeratorByExpirationDate(
                        format.parse(format.format(start)),
                        format.parse(format.format(end)),
                        refrigeratorId
                );

        if (items == null){
            response = new ResponseEntity<>("Failed to retrieve items in refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(items, HttpStatus.OK);
            logger.info("Refrigerator retrieved");
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
    public ResponseEntity<Object> addItemInRefrigerator(@RequestBody ItemInRefrigeratorCreationDto dto){
        ResponseEntity<Object> response = validateItemInRefrigeratorCreationDto(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info((String) response.getBody());
            return response;
        }

        boolean refrigeratorContainsItem = refrigeratorServices.
                refrigeratorContainsItem(
                        dto.getItemName(),
                        dto.getRefrigeratorId()
                );

        if (refrigeratorContainsItem && refrigeratorServices.addExpirationDateItem(dto)
                    || !refrigeratorContainsItem && refrigeratorServices.addItemInRefrigerator(dto)) {
            response = new ResponseEntity<>("New item is added to database", HttpStatus.CREATED);
        }
        else {
            response = new ResponseEntity<>("Item is not added to refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
           }
        logger.info((String) response.getBody());
        return response;
    }

    @PutMapping("/updateItemInRefrigerator")
    @Operation(summary = "Update item in a refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Date of the item is updated"),
            @ApiResponse(responseCode = "400", description = "Parameters are invalid"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Failed to update item")
    })
    public ResponseEntity<Object> updateItemInRefrigerator(@RequestBody EditItemInRefrigeratorDto dto) {
        ResponseEntity<Object> response = validateItemInRefrigeratorEditDto(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info((String) response.getBody());
            return response;
        }

        if(refrigeratorServices.updateItemInRefrigerator(dto)) {
            response = new ResponseEntity<>("Item is updated", HttpStatus.OK);
        }
        else {
            response = new ResponseEntity<>("Item is not updated", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    @DeleteMapping("/removeItem")
    @Operation(summary = "Remove item from refrigerator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Removed item from refrigerator"),
            @ApiResponse(responseCode = "404", description = "Refrigerator or/and item does not exist"),
            @ApiResponse(responseCode = "500", description = "Item is not removed from refrigerator")
    })
    public ResponseEntity<Object> removeItemFromRefrigerator(@RequestBody ItemInRefrigeratorRemovalDto dto) {
        ResponseEntity<Object> response;

        if (refrigeratorServices.itemExpirationDateNotExists(dto.getItemExpirationDateId())) {
            response = new ResponseEntity<>("Item does not exist in refrigerator", HttpStatus.NOT_FOUND);
        }
        else if(!dto.isGarbage() && refrigeratorServices.deleteItemFromRefrigerator(dto)){
            response = new ResponseEntity<>("Item is removed from refrigerator", HttpStatus.OK);
        }
        else if(dto.isGarbage() && garbageServices.addToGarbage(dto) && refrigeratorServices.deleteItemFromRefrigerator(dto)){
            response = new ResponseEntity<>("Item is removed from refrigerator and thrown in garbage", HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>("Item is not removed from refrigerator", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String) response.getBody());
        return response;
    }

    /**
     *
     * Method to validate the input
     * @param dto Dto object containing data to be validated
     * @return Different HTTP status messages based on the validity of the data
     */
    private ResponseEntity<Object> validateItemInRefrigeratorCreationDto(ItemInRefrigeratorCreationDto dto){
        ResponseEntity<Object> response;
        if (dto.getRefrigeratorId() == -1 || dto.getItemName().isEmpty()
                || dto.getAmount() == 0 || dto.getMeasurementType() == null){
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if(refrigeratorServices.refrigeratorNotExists(dto.getRefrigeratorId())){
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
        }
        else if(itemServices.itemNotExist(dto.getItemName())){
            response = new ResponseEntity<>("Item does not exist", HttpStatus.NOT_FOUND);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }

    /**
     *
     * Method to validate the input
     * @param dto Dto object containing data to be validated
     * @return Different HTTP status messages based on the validity of the data
     */
    private ResponseEntity<Object> validateItemInRefrigeratorEditDto(EditItemInRefrigeratorDto dto){
        ResponseEntity<Object> response;
        if (dto.getAmount() == 0 || dto.getMeasurementType() == null || dto.getDate() == null){
            response = new ResponseEntity<>("Data is not valid", HttpStatus.BAD_REQUEST);
        }
        else if(refrigeratorServices.itemExpirationDateNotExists(dto.getItemExpirationDateId())){
            response = new ResponseEntity<>("Item does not exist in refrigerator", HttpStatus.NOT_FOUND);
        }
        else if(dto.getDate().before(Calendar.getInstance().getTime())){
            response = new ResponseEntity<>("New date is in the past", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }
}