package idatt2106v231.backend.controller;

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

@RestController
@RequestMapping("/api/shoppingList")
@CrossOrigin("http://localhost:8000/")
public class ShoppingListController {

    private final Logger logger;

    private final ShoppingListServices shoppingListServices;
    private final UserServices userServices;
    private final ItemServices itemServices;
    private final SubUserServices subUserServices;

    @Autowired
    public ShoppingListController(ShoppingListServices shoppingListServices, UserServices userServices, ItemServices itemServices,
                                  SubUserServices subUserServices) {
        this.shoppingListServices = shoppingListServices;
        this.userServices = userServices;
        this.itemServices = itemServices;
        this.subUserServices = subUserServices;
        this.logger = LoggerFactory.getLogger(SubUserServices.class);
    }

    @GetMapping("/getItemsFromShoppingList")
    @Operation(summary = "Get all items from a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved items from database"),
            @ApiResponse(responseCode = "400", description = "User does not exist in database")
    })
    public ResponseEntity<Object> getAllItemsFromShoppingList(@RequestParam String email) {
        ResponseEntity<Object> response;

        if(userServices.userNotExists(email)) {
            response = new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
        } else {
            response = new ResponseEntity<>(shoppingListServices.getShoppingListByUserEmail(email), HttpStatus.OK);
            logger.info("Retrieving all items in shopping list with email: " + email);
        }
        return response;
    }

    @PostMapping("/addItemToShoppingList")
    @Operation(summary = "Add item to a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added item to shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> addItemToShoppingList(@RequestBody ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response = validateItemShoppingListDto(itemInShoppingListCreationDto);

        if (subUserServices.subUserNotExists(itemInShoppingListCreationDto.getSubUserId())) {
            response = new ResponseEntity<>("Sub user does not exist", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
            return response;
        }

        if (response.getStatusCode() != HttpStatus.OK){
            logger.info((String)response.getBody());
            return response;
        }


        //Updates the amount instead of creating new row if the item already exists in the db
        if (shoppingListServices.itemInShoppinglistExistsWithAccessLevel(
                itemInShoppingListCreationDto.getShoppingListId(),
                itemInShoppingListCreationDto.getItemName(),
                subUserServices.getAccessLevel(itemInShoppingListCreationDto.getSubUserId()))) {

            shoppingListServices.updateAmount(itemInShoppingListCreationDto);

            response = new ResponseEntity<>("Updated amount of the item", HttpStatus.OK);
        } else {
            shoppingListServices.addItemToShoppingList(itemInShoppingListCreationDto);
            response = new ResponseEntity<>("Item saved to shopping list", HttpStatus.CREATED);
        }

        logger.info((String)response.getBody());
        return response;
    }

    @DeleteMapping("/deleteItemFromShoppingList")
    @Operation(summary = "Delete item from shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted item from shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> deleteItemFromShoppingList(@RequestBody ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response = validateItemShoppingListDto(itemInShoppingListCreationDto);

        if (response.getStatusCode() == HttpStatus.OK){

            if(shoppingListServices.deleteItemFromShoppingList(itemInShoppingListCreationDto)) {
                response = new ResponseEntity<>("Item was updated", HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Could not delete item from shopping list", HttpStatus.NOT_FOUND);
            }
        }

        logger.info((String)response.getBody());
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
            logger.info((String)response.getBody());
            return response;
        }

        if (shoppingListServices.addWeeklyMenuToShoppingList(dto)) {
            response = new ResponseEntity<>("Items added to shopping list", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Failed to add items to shopping list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    @GetMapping("/addMostPopularItems")
    @Operation(summary = "Add 5 most popular fridge items to a shopping list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items added to shopping list"),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid")
    })
    public ResponseEntity<Object> addMostPopularItems(@RequestParam int shoppingListId, @RequestParam int subUserId) {

        ResponseEntity<Object> response;

        if (shoppingListServices.shoppingListNotExists(shoppingListId)) {
            response = new ResponseEntity<>("Shopping list does not exist", HttpStatus.NOT_FOUND);
        }
        else if (subUserServices.subUserNotExists(subUserId)) {
            response = new ResponseEntity<>("Sub user does not exists", HttpStatus.NOT_FOUND);
        }
        else if (!subUserServices.getMasterUserEmail(subUserId)
                .equals(shoppingListServices.getShoppingListUserEmail(shoppingListId))) {
            response = new ResponseEntity<>("Sub user does not have access to this shopping list", HttpStatus.BAD_REQUEST);
        }
        else if (shoppingListServices.addMostPopularItems(shoppingListId, subUserId)) {
            response = new ResponseEntity<>("Successfully added popular items that are not already in the shopping list", HttpStatus.OK);
        }
        else {
            response = new ResponseEntity<>("Failed to add popular items to the shopping list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    /**
     *
     * Method to validate the input
     * @param itemInShoppingListCreationDto Dto object containing data to be validated
     * @return Different HTTP status messages based on the validity of the data
     */
    public ResponseEntity<Object> validateItemShoppingListDto(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        ResponseEntity<Object> response;

        if(shoppingListServices.shoppingListNotExists(itemInShoppingListCreationDto.getShoppingListId())) {
            response = new ResponseEntity<>("User doesnt exist", HttpStatus.BAD_REQUEST);
        }
        else if(subUserServices.subUserNotExists(itemInShoppingListCreationDto.getSubUserId())) {
            response = new ResponseEntity<>("Sub user doesnt exist", HttpStatus.BAD_REQUEST);
        }
        else if(itemServices.itemNotExist(itemInShoppingListCreationDto.getItemName())) {
            response = new ResponseEntity<>("Item doesnt exist", HttpStatus.BAD_REQUEST);
        }
        else if(itemInShoppingListCreationDto.getAmount() <= 0) {
            response = new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        else if(itemInShoppingListCreationDto.getMeasurementType() == null) {
            response = new ResponseEntity<>("Measurement is not specified", HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Object> validateWeeklyMenuShoppingListDto(WeeklyMenuShoppingListDto dto) {
        ResponseEntity<Object> response;

        if (shoppingListServices.shoppingListNotExists(dto.getShoppingListId())) {
            response = new ResponseEntity<>("Shoppinglist does not exist", HttpStatus.NOT_FOUND);
        }
        else if (subUserServices.getMasterUserEmail(dto.getSubUserId()) == null) {
            response = new ResponseEntity<>("Sub user does not exist", HttpStatus.BAD_REQUEST);
        }
        else if (!subUserServices.getMasterUserEmail(dto.getSubUserId()).equals(
                shoppingListServices.getShoppingListUserEmail(dto.getShoppingListId()))) {
            response = new ResponseEntity<>("Sub user does not have access to this shopping list", HttpStatus.BAD_REQUEST);
        }
        else if (dto.getIngredients().size() == 0) {
            response = new ResponseEntity<>("Ingredients list is empty", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.OK);
        }

        return response;
    }
}
