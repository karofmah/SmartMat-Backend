package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.WeeklyMenuShoppingListDto;
import idatt2106v231.backend.service.ItemServices;
import idatt2106v231.backend.service.ShoppingListServices;
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

@RestController
@RequestMapping("/api/shoppingList")
@CrossOrigin("http://localhost:8000/")
public class ShoppingListController {

    private static final Logger logger = LoggerFactory.getLogger(SubUserServices.class);

    @Autowired
    private ShoppingListServices shoppingListServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private ItemServices itemServices;

    @GetMapping("/getItemsFromShoppingList")
    @Operation(summary = "Get all items from a shoppinglist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved items from database"),
            @ApiResponse(responseCode = "400", description = "User does not exist in database")
    })
    public ResponseEntity<Object> getAllItemsFromShoppingList(@RequestParam String email) {
        ResponseEntity<Object> response;

        if(!userServices.checkIfUserExists(email)) {
            response = new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        } else {
            response = new ResponseEntity<>(shoppingListServices.getShoppingListByUserEmail(email), HttpStatus.OK);
            logger.info("Retrieving all items in shoppinglist with email: " + email);
        }
        return response;
    }

    @PostMapping("/addItemToShoppingList")
    @Operation(summary = "Add item to a shoppinglist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added item to shoppinglist"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> addItemToShoppingList(@RequestBody ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response = validateItemShoppingListDto(itemInShoppingListCreationDto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }

        /*if(itemInShoppingListCreationDto.getMasterUserEmail() == null || itemInShoppingListCreationDto.getSubUserName() == null) {
            response = new ResponseEntity<>("Subuser is not defined", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
            return response;
        }*/

        if(shoppingListServices.itemExistsInShoppingList(itemInShoppingListCreationDto.getShoppingListId(), itemInShoppingListCreationDto.getItemName())) {
            response = new ResponseEntity<>("Item already exists in shoppinglist", HttpStatus.CONFLICT);
            logger.info(response.getBody() + "");
            return response;
        }

        shoppingListServices.saveItemToShoppingList(itemInShoppingListCreationDto);
        response = new ResponseEntity<>("Item saved to shoppinglist", HttpStatus.OK);

        logger.info(response.getBody() + "");
        return response;
    }

    @DeleteMapping("/deleteItemFromShoppingList")
    @Operation(summary = "Delete item from shoppinglist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted item from shoppinglist"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> deleteItemFromShoppingList(@RequestBody ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response = validateItemShoppingListDto(itemInShoppingListCreationDto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }

        shoppingListServices.deleteItemFromShoppingList(itemInShoppingListCreationDto);
        response = new ResponseEntity<>("Item deleted from shoppinglist", HttpStatus.OK);
        logger.info(response.getBody() + "");
        return response;
    }

    @PostMapping("/addWeeklyMenuToShoppingList")
    @Operation(summary = "Add ingredients from a weekly menu to a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items added to shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> addWeeklyMenuToShoppingList(@RequestBody WeeklyMenuShoppingListDto dto) {
        ResponseEntity<Object> response = validateWeeklyMenuShoppingListDto(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }

        shoppingListServices.addWeeklyMenuToShoppingList(dto);
        response = new ResponseEntity<>("Ingredients added to shopping list", HttpStatus.OK);
        logger.info(response.getBody() + "");
        return response;
    }

    public ResponseEntity<Object> validateItemShoppingListDto(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response;

        if(!shoppingListServices.shoppingListExists(itemInShoppingListCreationDto.getShoppingListId())) {
            response = new ResponseEntity<>("User doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(!itemServices.checkIfItemExists(itemInShoppingListCreationDto.getItemName())) {
            response = new ResponseEntity<>("Item doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(itemInShoppingListCreationDto.getAmount() == 0) {
            response = new ResponseEntity<>("Amount is not specified", HttpStatus.BAD_REQUEST);
        } else if(itemInShoppingListCreationDto.getMeasurementType() == null) {
            response = new ResponseEntity<>("Measurement is not specified", HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<Object> validateWeeklyMenuShoppingListDto(WeeklyMenuShoppingListDto weeklyMenuShoppingListDto) {
        ResponseEntity<Object> response;

        if (!shoppingListServices.shoppingListExists(weeklyMenuShoppingListDto.getShoppingListId())) {
            response = new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        } else if (weeklyMenuShoppingListDto.getIngredients().size() == 0) {
            response = new ResponseEntity<>("Ingredients list is empty", HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }

        return response;
    }
}
