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
     * This method retrieves a user with a specified email address from the database.
     *
     * @param refrigeratorId the id of the refrigerator to generate a recipe from.
     * @return an Optional object containing the user with the specified email address, or an empty Optional object if the user does not exist in the database.
     */
    public String generateRecipe(int refrigeratorId) {
        try {
            // TODO Implement random recipe based on refrigerator contents

            String query = "I need a random recipe";

            String result = aiServices.getChatCompletion(query);

            return result;
        } catch (IllegalArgumentException e){
            _logger.error("Failed to generate recipe", e);
            return null;
        }
    }


}