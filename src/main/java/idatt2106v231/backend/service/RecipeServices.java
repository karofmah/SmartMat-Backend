package idatt2106v231.backend.service;

import idatt2106v231.backend.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private AiServices aiServices;

    private RefrigeratorServices refrigeratorServices;


    /**
     * Sets the AI Service for AI queries.
     *
     * @param aiServices the service to use.
     */
    @Autowired
    public void setAiServices(AiServices aiServices) {
        this.aiServices = aiServices;
    }

    @Autowired
    public void setRefrigeratorServices(RefrigeratorServices refrigeratorServices) {
        this.refrigeratorServices = refrigeratorServices;
    }

    /**
     * This method generates a random recipe based on items in a refrigerator.
     *
     * @param refrigeratorId the id of the refrigerator to generate a recipe from.
     * @return A String containing the generated recipe.
     */
    public String generateRecipe(int refrigeratorId) {
        try {

            //List<Item> ingredients = refrigeratorServices.getItemsInRefrigerator(refrigeratorId);
            List<Item> ingredients = null; // TODO Implement fridge connection

            StringBuilder query = new StringBuilder("A recipe that includes the ingredients ");

            for (Item ingredient : ingredients) {
                query.append(ingredient.getName()).append(" ");
            }

            return aiServices.getChatCompletion(query.toString());
        } catch (IllegalArgumentException e){
            _logger.error("Failed to generate recipe", e);
            return null;
        }
    }


}