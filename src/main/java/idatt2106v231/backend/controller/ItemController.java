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

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Item API", description = "API for managing items")
public class ItemController {

    private final Logger logger;

    private final ItemServices services;

    private final CategoryServices categoryServices;

    @Autowired
    public ItemController(ItemServices services, CategoryServices categoryServices) {
        this.services = services;
        this.categoryServices = categoryServices;
        this.logger = LoggerFactory.getLogger(ItemController.class);
    }

    @PostMapping("/saveItem")
    @Operation(summary = "Save new item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item is added to database"),
            @ApiResponse(responseCode = "226", description = "Item already exists"),
            @ApiResponse(responseCode = "400", description = "Data is not specified"),
            @ApiResponse(responseCode = "404", description = "Category does not exist"),
            @ApiResponse(responseCode = "500", description = "Failed to save item")
    })
    public ResponseEntity<Object> saveItem(@RequestBody ItemDto item) {
        ResponseEntity<Object> response = validateItemDto(item);

        if(response.getStatusCode().equals(HttpStatus.OK)){
            if (services.saveItem(item)){
                response = new ResponseEntity<>("Item saved to database", HttpStatus.CREATED);
            } else {
                response =  new ResponseEntity<>("Failed to save item", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info((String)response.getBody());
        return response;
    }

    @DeleteMapping("/deleteItem/{itemId}")
    @Operation(summary = "Delete item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item is removed from database"),
            @ApiResponse(responseCode = "404", description = "Item does not exist in database"),
            @ApiResponse(responseCode = "500", description = "Failed to delete item")
    })
    public ResponseEntity<Object> deleteItem(@PathVariable int itemId) {
        ResponseEntity<Object> response;

        if (services.itemNotExist(itemId)){
            response = new ResponseEntity<>("Item does not exist", HttpStatus.NOT_FOUND);
        }
        else if (services.deleteItem(itemId)){
            response = new ResponseEntity<>("Item removed from database", HttpStatus.OK);
        }
        else{
            response =  new ResponseEntity<>("Failed to delete item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    @GetMapping("/getItem")
    @Operation(summary = "Get item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the item"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve item")
    })
    public ResponseEntity<Object> getItemByName(@RequestParam("name") String name) {
        ResponseEntity<Object> response;

        if (services.itemNotExist(name)){
            response = new ResponseEntity<>("Item does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
        }
        else {
            response = getItem(services.getItemByName(name));

        }
        return response;
    }

    @GetMapping("/getItem/{id}")
    @Operation(summary = "Get item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the item"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve item")
    })
    public ResponseEntity<Object> getItemById(@PathVariable("id") Integer id) {
        ResponseEntity<Object> response;

        if (services.itemNotExist(id)){
            response = new ResponseEntity<>("Item does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
        } else {
            response = getItem(services.getItemById(id));

        }
        return response;
    }

    @GetMapping("/getAllItems")
    @Operation(summary = "Get all items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the items"),
            @ApiResponse(responseCode = "404", description = "Item are not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve items")
    })
    public ResponseEntity<Object> getAllItems() {
        ResponseEntity<Object> response;
        List<ItemDto> items = services.getAllItems();
        if (items == null){
            response = new ResponseEntity<>("Failed to retrieve items", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else if (items.isEmpty()){
            response = new ResponseEntity<>("There are no items registered in the database", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(items, HttpStatus.OK);
            logger.info("Items retrieved");
        }
        return response;
    }

    @GetMapping("/getAllItemsByCategory")
    @Operation(summary = "Get all items by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved the items"),
            @ApiResponse(responseCode = "204", description = "No content in database"),
            @ApiResponse(responseCode = "400", description = "Category does not exist"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve items")
    })
    public ResponseEntity<Object> getAllItemsByCategory(@RequestParam int categoryId) {
        ResponseEntity<Object> response;
        if(categoryServices.categoryNotExist(categoryId)){
            response = new ResponseEntity<>("Category does not exist", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
            return response;
        }

        List<ItemDto> items = services.getAllItemsByCategory(categoryId);
        if (items == null){
            response = new ResponseEntity<>("Failed to retrieve items", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else if (items.isEmpty()){
            response = new ResponseEntity<>("There are no items registered under this category", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(items, HttpStatus.OK);
            logger.info("Items retrieved");
        }
        return response;
    }

    /**
     *
     * Method to return correct item. Logs correct message.
     * @param item To validate and return
     * @return Correct responseentity based on the item, and the item if it passes the test
     */
    private ResponseEntity<Object> getItem(ItemDto item) {
        ResponseEntity<Object> response;
        if (item == null) {
            response = new ResponseEntity<>("Failed to retrieve item", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        } else {
            response = new ResponseEntity<>(item, HttpStatus.OK);
            logger.info("Item retrieved");
        }
        return response;
    }

    /**
     *
     * Method to validate the input
     * @param dto Dto object containing data to be validated
     * @return Different HTTP status messages based on the validity of the data
     */
    private ResponseEntity<Object> validateItemDto(ItemDto dto){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        if (dto.getName().isEmpty() || dto.getCategoryId() == -1){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (!services.itemNotExist(dto.getName())){
            response = new ResponseEntity<>("Item already exists", HttpStatus.IM_USED);
        }
        else if (categoryServices.categoryNotExist(dto.getCategoryId())){
            response =  new ResponseEntity<>("Category does not exist", HttpStatus.NOT_FOUND);
        }
        return response;
    }
}