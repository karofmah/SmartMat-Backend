
package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.RecipeServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import idatt2106v231.backend.service.RefrigeratorServices;
import idatt2106v231.backend.service.UserServices;
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
    private final RefrigeratorServices refrigeratorServices;
    private final UserServices userServices;

    @Autowired
    public RecipeController(RecipeServices recipeServices, RefrigeratorServices refrigeratorServices,
                            UserServices userServices) {
        this.recipeServices = recipeServices;
        this.refrigeratorServices = refrigeratorServices;
        this.userServices = userServices;
        this.logger = LoggerFactory.getLogger(RecipeController.class);
    }

    @GetMapping("/generateRecipe")
    @Operation(summary = "Generate new recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate recipe")
    })
    public ResponseEntity<String> generateRecipe(@RequestParam int refrigeratorId) {

        ResponseEntity<String> response;

        if (refrigeratorServices.refrigeratorNotExists(refrigeratorId)){
            response = new ResponseEntity<>("Refrigerator does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        String recipe = recipeServices.generateRecipe(refrigeratorId);

        if (recipe == null || recipe.startsWith("ERROR: ")) {
            response = new ResponseEntity<>("Could not generate recipe", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error(recipe);
        } else {
            response = new ResponseEntity<>(recipe, HttpStatus.OK);
            logger.info("Recipe generated");
        }
        return response;
    }

    @GetMapping("/getWeeklyMenu/{userEmail}")
    @Operation(summary = "Retrieve a users weekly menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly menu retrieved"),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve weekly menu")
    })
    public ResponseEntity<Object> getWeeklyMenu(@PathVariable("userEmail") String userEmail) {

        ResponseEntity<Object> response;

        if (userServices.userNotExists(userEmail)){
            response = new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        }

        String weeklyMenu = recipeServices.getWeeklyMenu(userEmail);

        if (weeklyMenu == null) {
            response = new ResponseEntity<>("Failed to get weekly menu", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error("Failed to get weekly menu");
        } else {
            response = new ResponseEntity<>(weeklyMenu, HttpStatus.OK);
            logger.info("Weekly menu retrieved");
        }
        return response;
    }

    @Operation(summary = "Generate weekly menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly menu generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate weekly menu")
    })
    @GetMapping("/generateWeeklyMenu/{userEmail}")
    public ResponseEntity<Object> generateWeeklyMenu(@PathVariable("userEmail") String userEmail, @RequestParam int numPeople) {

        ResponseEntity<Object> response;

        if (userServices.userNotExists(userEmail)){
            response = new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            logger.info((String)response.getBody());
            return response;
        } else if (numPeople < 1) {
            response = new ResponseEntity<>("Number of people must be at least 1", HttpStatus.BAD_REQUEST);
            logger.info((String)response.getBody());
            return response;
        }

        String weeklyMenu = recipeServices.generateWeeklyMenu(userEmail, numPeople);

        if (weeklyMenu == null || weeklyMenu.startsWith("ERROR: ")) {
            response = new ResponseEntity<>("Could not generate weekly menu", HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error(weeklyMenu);
        } else {
            response = new ResponseEntity<>(weeklyMenu, HttpStatus.OK);
            logger.info("Weekly menu generated");
        }
        return response;
    }
}