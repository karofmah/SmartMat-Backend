
package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.AiServices;
import idatt2106v231.backend.service.RecipeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin("http://localhost:8000/")
public class RecipeController {

    private RecipeServices recipeServices;

    @Autowired
    public void setRecipeServices(RecipeServices recipeServices) {
        this.recipeServices = recipeServices;
    }

    @GetMapping("/generateRecipe")
    public ResponseEntity<String> generateRecipe(@RequestParam int refrigeratorId){
        try{

            String recipe = recipeServices.generateRecipe(refrigeratorId);

            return new ResponseEntity<>(recipe, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}