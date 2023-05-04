
package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.AiServices;
import idatt2106v231.backend.service.RecipeServices;
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
@RequestMapping("/api/recipes")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Recipe API", description = "API for managing Recipes")
public class RecipeController {

    private final Logger logger;

    private final RecipeServices recipeServices;

    @Autowired
    public RecipeController(RecipeServices recipeServices) {
        this.recipeServices = recipeServices;
         this.logger = LoggerFactory.getLogger(RecipeController.class);
    }

    @GetMapping("/generateRecipe")
    @Operation(summary = "Generate new recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate recipe")
    })
    public ResponseEntity<String> generateRecipe(@RequestParam int refrigeratorId) {
        try{
            String recipe = recipeServices.generateRecipe(refrigeratorId);

            ResponseEntity<String> response = new ResponseEntity<>(recipe, HttpStatus.OK);
            logger.info("Recipe generated");

            return response;
        }catch (Exception e){
            logger.info("Failed to create recipe");

            return new ResponseEntity<>("Failed to create recipe", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getWeeklyMenu/{userEmail}")
    @Operation(summary = "Retrieve a users weekly menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly menu retrieved"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve weekly menu")
    })
    public ResponseEntity<String> getWeeklyMenu(@PathVariable("userEmail") String userEmail) {
        try {
            String recipe = recipeServices.getWeeklyMenu(userEmail);

            ResponseEntity<String> response = new ResponseEntity<>(recipe, HttpStatus.OK);
            logger.info("Weekly menu retrieved");

            return response;
        } catch (Exception e){
            logger.info("Failed to retrieve weekly menu");

            return new ResponseEntity<>("Failed to retrieve weekly menu", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Generate weekly menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly menu generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate weekly menu")
    })
    @GetMapping("/generateWeeklyMenu/{userEmail}")
    public ResponseEntity<String> generateWeeklyMenu(@PathVariable("userEmail") String userEmail, @RequestParam int numPeople) {
        try {
            String weeklyMenu = recipeServices.generateWeeklyMenu(userEmail, numPeople);

            ResponseEntity<String> response = new ResponseEntity<>(weeklyMenu, HttpStatus.OK);
            logger.info("Weekly menu generated generated");

            return response;

        } catch (Exception e){
            logger.info("Failed to generate weekly menu");

            return new ResponseEntity<>("Failed to generate weekly menu", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO Remove endpoint
    @Autowired
    AiServices aiServices;
    @GetMapping("/askAi")
    public ResponseEntity<String> askAi(@RequestParam String text) {
        try {
            return new ResponseEntity<>(aiServices.getChatCompletion(text), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}