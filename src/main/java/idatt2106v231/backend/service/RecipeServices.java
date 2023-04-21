package idatt2106v231.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private AiServices aiServices;


    /**
     * Sets the AI Service for AI queries.
     *
     * @param aiServices the service to use.
     */
    @Autowired
    public void setAiServices(AiServices aiServices) {
        this.aiServices = aiServices;
    }

    /**
     * This method generates a random recipe based on items in a refrigerator.
     *
     * @param refrigeratorId the id of the refrigerator to generate a recipe from.
     * @return A String containing the generated recipe.
     */
    public String generateRecipe(int refrigeratorId) {
        try {
            // TODO Implement random recipe based on refrigerator contents

            String query = "I need a random recipe";

            return aiServices.getChatCompletion(query);
        } catch (IllegalArgumentException e){
            _logger.error("Failed to generate recipe", e);
            return null;
        }
    }


}