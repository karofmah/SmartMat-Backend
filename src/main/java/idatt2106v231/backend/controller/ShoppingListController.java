package idatt2106v231.backend.controller;

import idatt2106v231.backend.model.User;
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

    @GetMapping("/getItemsFromShoppingList")
    @Operation(summary = "Get all items from a shoppinglist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved items from database"),
            @ApiResponse(responseCode = "400", description = "User does not exist in database")
    })
    public ResponseEntity<Object> getAllItemsFromShoppingList(@RequestParam String email) {
        ResponseEntity<Object> response;

        if(!userServices.userExists(email)) {
            response = new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            logger.info(response.getBody() + "");
        } else {
            response = new ResponseEntity<>(shoppingListServices.getAllItems(email), HttpStatus.OK);
            logger.info("Retrieving all items in shoppinglist with email: " + email);
        }
        return response;
    }
}
