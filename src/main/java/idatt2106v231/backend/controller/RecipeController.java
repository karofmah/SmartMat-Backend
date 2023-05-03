
package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.RecipeServices;
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
    public ResponseEntity<String> generateRecipe(@RequestParam int refrigeratorId) {
        try{

            String recipe = recipeServices.generateRecipe(refrigeratorId);

            return new ResponseEntity<>(recipe, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/getWeeklyMenu/{userEmail}")
    public ResponseEntity<String> getWeeklyMenu(@PathVariable("userEmail") String userEmail) {
        try {

            String recipe = recipeServices.getWeeklyMenu(userEmail);

            return new ResponseEntity<>(recipe, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/generateWeeklyMenu/{userEmail}")
    public ResponseEntity<String> generateWeeklyMenu(@PathVariable("userEmail") String userEmail, @RequestParam int numPeople) {
        try {

            String weeklyMenu = recipeServices.generateWeeklyMenu(userEmail, numPeople);

            return new ResponseEntity<>(weeklyMenu, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}