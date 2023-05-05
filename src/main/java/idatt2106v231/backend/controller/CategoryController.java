package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.item.CategoryDto;
import idatt2106v231.backend.service.CategoryServices;
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
@RequestMapping("/api/categories")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Category API", description = "API for managing categories")
public class CategoryController {

    private final Logger logger;

    private final CategoryServices categoryServices;

    @Autowired
    public CategoryController(CategoryServices categoryServices) {
        this.categoryServices = categoryServices;
        this.logger = LoggerFactory.getLogger(CategoryController.class);
    }

    @PostMapping("/saveCategory")
    @Operation(summary = "Save new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category added to database"),
            @ApiResponse(responseCode = "226", description = "Category already exists"),
            @ApiResponse(responseCode = "500", description = "Failed to save category to database")
    })
    public ResponseEntity<Object> saveCategory(@RequestBody CategoryDto categoryDto) {
        ResponseEntity<Object> response;
        if (categoryServices.categoryExist(categoryDto.getDescription())){
            response = new ResponseEntity<>("Category already exists", HttpStatus.IM_USED);
        }
        else if (categoryServices.saveCategory(categoryDto)){
            response = new ResponseEntity<>("Category is saved to database", HttpStatus.CREATED);
        }
        else{
            response = new ResponseEntity<>("Failed to save category", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info((String)response.getBody());
        return response;
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    @Operation(summary = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category is removed from database"),
            @ApiResponse(responseCode = "404", description = "Category does not exists"),
            @ApiResponse(responseCode = "500", description = "Failed to delete category")

    })
    public ResponseEntity<Object> deleteCategory(@PathVariable("categoryId") int categoryId) {
        ResponseEntity<Object> response;
        if (categoryServices.categoryNotExist(categoryId)){
            response = new ResponseEntity<>("Category does not exist", HttpStatus.NOT_FOUND);
        }
        else if (categoryServices.deleteCategory(categoryId)){
            response = new ResponseEntity<>("Category removed from database", HttpStatus.OK);
        }
        else {
            response = new ResponseEntity<>("Failed to delete category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info((String)response.getBody());
        return response;
    }

    @GetMapping("/getCategory/{categoryId}")
    @Operation(summary = "Get category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the category"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve category")
    })
    public ResponseEntity<Object> getCategoryById(@PathVariable("categoryId") Integer categoryId) {
        ResponseEntity<Object> response;

        if (categoryServices.categoryNotExist(categoryId)){
            response = new ResponseEntity<>("Category does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        CategoryDto category = categoryServices.getCategory(categoryId);
        if (category == null){
            response = new ResponseEntity<>("Failed to retrieve category", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(category, HttpStatus.OK);
            logger.info("Category retrieved");
        }
        return response;
    }

    @GetMapping("/getAllCategories")
    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned the categories"),
            @ApiResponse(responseCode = "404", description = "No content in database"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve categories")
    })
    public ResponseEntity<Object> getAllCategories() {
        ResponseEntity<Object> response;

        List<CategoryDto> categories = categoryServices.getAllCategories();
        if (categories == null){
            response = new ResponseEntity<>("Failed to retrieve categories", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.info((String)response.getBody());
        }
        else if (categories.isEmpty()){
            response = new ResponseEntity<>("There are no categories registered in the database", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
        }
        else {
            response = new ResponseEntity<>(categories, HttpStatus.OK);
            logger.info("Categories retrieved");
        }
        return response;
    }
}