package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.service.CategoryServices;
import idatt2106v231.backend.service.ItemServices;
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
@RequestMapping("/api/items")
@CrossOrigin("*")
@Tag(name = "Item API", description = "API for managing Items")
public class ItemController {

    @Autowired
    private ItemServices services;
    @Autowired
    private CategoryServices categoryServices;
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @PostMapping("/saveItem")
    @Operation(summary = "Save new item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item is added to database"),
            @ApiResponse(responseCode = "500", description = "Item was not added to the database")
    })
    public ResponseEntity<Object> saveItem(@RequestBody ItemDto item) {
        if (services.checkIfItemExists(item.getName())){
            return new ResponseEntity<>("Item already exists", HttpStatus.IM_USED);
        }

        ResponseEntity<Object> response = validateItemDto(item);

        if (response.getStatusCode().equals(HttpStatus.OK)){
            if (services.saveItem(item)){
                response = new ResponseEntity<>("Item is saved to database", HttpStatus.OK);
            }else{
                response =  new ResponseEntity<>("Data is not valid", HttpStatus.INTERNAL_SERVER_ERROR);                logger.info(response.getBody() + "");
                logger.info(response.getBody() + "");
            }
        }
        return response;
    }

    @PostMapping("/deleteItem")
    @Operation(summary = "Delete item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item is removed from database"),
            @ApiResponse(responseCode = "404", description = "Item does not exist in database")
    })
    public ResponseEntity<Object> deleteItem(@RequestBody int itemId) {
        if (!services.checkIfItemExists(itemId)){
            return new ResponseEntity<>("Item does not exists", HttpStatus.NOT_FOUND);
        }
        services.deleteItem(itemId);
        return new ResponseEntity<>("Item removed from database", HttpStatus.OK);
    }

    private ResponseEntity<Object> validateItemDto(ItemDto dto){
        if (dto.getName().isEmpty() || dto.getCategory() == -1){
            return new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        if (categoryServices.categoryExist(dto.getCategory())){
            return new ResponseEntity<>("Category does not exist", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getItem")
    @Operation(summary = "Get item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the item"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Object> getItem(@RequestParam String name) {
        return null;
    }

    @GetMapping("/getItem")
    @Operation(summary = "Get item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the item"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Object> getItemById(@PathVariable("id") Integer id) {
        return null;
    }

    @GetMapping("/getAllItems")
    public ResponseEntity<Object> getAllItem(@RequestParam String name) {
        return null;
    }

    @GetMapping("/getAllItemsByCategory")
    public ResponseEntity<Object> getAllItemsByCategory(@RequestParam String name) {
        return null;
    }

    @GetMapping("/getAllItemsInRefrigerator")
    public ResponseEntity<Object> getAllItemsInRefrigerator(@RequestParam int refrigeratorId) {
        return null;
    }
}