package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.WeeklyMenuShoppingListDto;
import idatt2106v231.backend.service.*;
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

    @Autowired
    private SubUserServices subUserServices;

    @Autowired
    private RefrigeratorServices refrigeratorServices;

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

        if (!subUserServices.subUserExists(itemInShoppingListCreationDto.getSubUserId())) {
            response = new ResponseEntity<>("Sub user does not exist", HttpStatus.BAD_REQUEST);
        }

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }


        if (shoppingListServices.itemExistsWithAccessLevel(
                itemInShoppingListCreationDto.getShoppingListId(),
                itemInShoppingListCreationDto.getItemName(),
                subUserServices.getAccessLevel(itemInShoppingListCreationDto.getSubUserId()))) {

            shoppingListServices.updateAmount(itemInShoppingListCreationDto);

            response = new ResponseEntity<>("Updated amount of the item", HttpStatus.OK);
        } else {
            shoppingListServices.saveItemToShoppingList(itemInShoppingListCreationDto);
            response = new ResponseEntity<>("Item saved to shoppinglist", HttpStatus.OK);
        }


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

    @PostMapping("/addWeeklyMenu")
    @Operation(summary = "Add ingredients from a weekly menu to a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items added to shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> addWeeklyMenu(@RequestBody WeeklyMenuShoppingListDto dto) {
        ResponseEntity<Object> response = validateWeeklyMenuShoppingListDto(dto);

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info(response.getBody() + "");
            return response;
        }

        if (shoppingListServices.addWeeklyMenuToShoppingList(dto)) {
            response = new ResponseEntity<>("Items added to shopping list", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Failed to add items to shopping list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info(response.getBody() + "");
        return response;
    }

    @PostMapping("/magicWand")
    @Operation(summary = "Add 5 most popular fridge items to a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items added to shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> magicWand(@RequestParam int shoppingListId, int subUserId) {

        ResponseEntity<Object> response;

        if (!shoppingListServices.shoppingListExists(shoppingListId)) {
            response = new ResponseEntity<>("Shopping list does not exist", HttpStatus.NOT_FOUND);
        } else if (!subUserServices.subUserExists(subUserId)) {
            response = new ResponseEntity<>("Sub user does not exists", HttpStatus.NOT_FOUND);
        } else if (!subUserServices.getMasterUser(subUserId).getEmail()
                .equals(shoppingListServices.getUserEmail(shoppingListId))) {
            response = new ResponseEntity<>("Sub user does not have access to this shopping list", HttpStatus.BAD_REQUEST);
        } else if (shoppingListServices.magicWand(shoppingListId, subUserId)) {
            response = new ResponseEntity<>("Successfully added popular items to the shopping list", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Failed to add popular items to the shopping list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info(response.getBody() + "");
        return response;
    }

    public ResponseEntity<Object> validateItemShoppingListDto(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response;

        if(!shoppingListServices.shoppingListExists(itemInShoppingListCreationDto.getShoppingListId())) {
            response = new ResponseEntity<>("User doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(!itemServices.checkIfItemExists(itemInShoppingListCreationDto.getItemName())) {
            response = new ResponseEntity<>("Item doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(itemInShoppingListCreationDto.getAmount() <= 0) {
            response = new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        } else if(itemInShoppingListCreationDto.getMeasurementType() == null) {
            response = new ResponseEntity<>("Measurement is not specified", HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<Object> validateWeeklyMenuShoppingListDto(WeeklyMenuShoppingListDto dto) {
        ResponseEntity<Object> response;


        if (!shoppingListServices.shoppingListExists(dto.getShoppingListId())) {
            response = new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        } else if (subUserServices.getMasterUser(dto.getSubUserId()) == null) {
            response = new ResponseEntity<>("Sub user does not exist", HttpStatus.BAD_REQUEST);
        } else if (!subUserServices.getMasterUser(dto.getSubUserId()).getEmail().equals(
                shoppingListServices.getUserEmail(dto.getShoppingListId()))) {
            response = new ResponseEntity<>("Sub user does not have access to this shopping list", HttpStatus.BAD_REQUEST);
        } else if (dto.getIngredients().size() == 0) {
            response = new ResponseEntity<>("Ingredients list is empty", HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }

        return response;
    }
}
