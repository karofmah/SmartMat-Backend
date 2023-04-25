package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.shoppinglist.ItemShoppingListDto;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.service.ItemServices;
import idatt2106v231.backend.service.ShoppingListServices;
import idatt2106v231.backend.service.SubUserServices;
import idatt2106v231.backend.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.Response;
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
            response = new ResponseEntity<>(shoppingListServices.getAllItemsFromShoppingList(email), HttpStatus.OK);
            logger.info("Retrieving all items in shoppinglist with email: " + email);
        }
        return response;
    }

    @PostMapping("/addItemToShoppingList")
    @Operation(summary = "Add item to a shoppinglist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved items from database"),
            @ApiResponse(responseCode = "400", description = "User does not exist in database")
    })
    public ResponseEntity<Object> addItemToShoppingList(@RequestBody ItemShoppingListDto itemShoppingListDto) {
        ResponseEntity<Object> response;

        if(!userServices.checkIfUserExists(itemShoppingListDto.getUserEmail())) {
            response = new ResponseEntity<>("User doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(!itemServices.checkIfItemExists(itemShoppingListDto.getItemName())) {
            response = new ResponseEntity<>("Item doesnt exist", HttpStatus.BAD_REQUEST);
        } else if(itemShoppingListDto.getAmount() == 0) {
            response = new ResponseEntity<>("Amount is not specified", HttpStatus.BAD_REQUEST);
        } else if(itemShoppingListDto.getMeasurement() == null) {
            response = new ResponseEntity<>("Measurement is not specified", HttpStatus.BAD_REQUEST);
        } else {
            shoppingListServices.
        }

    }
}
