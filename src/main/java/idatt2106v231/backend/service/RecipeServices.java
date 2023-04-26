package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.repository.WeekMenuRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private AiServices aiServices;

    private RefrigeratorServices refrigeratorServices;

    private WeekMenuRepository weeklyMenuRepository;

    private UserServices userServices;

    private final ModelMapper mapper = new ModelMapper();

    private UserRepository userRepository;


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
     * Sets the refrigerator service
     * @param refrigeratorServices the service to use
     */
    @Autowired
    public void setRefrigeratorServices(RefrigeratorServices refrigeratorServices) {
        this.refrigeratorServices = refrigeratorServices;
    }

    /**
     * Sets the week menu repository
     * @param weeklyMenuRepository the repository to use
     */
    @Autowired
    public void setWeeklyMenuRepository(WeekMenuRepository weeklyMenuRepository) {
        this.weeklyMenuRepository = weeklyMenuRepository;
    }

    /**
     * Sets user services
     * @param userServices the user service
     */
    @Autowired
    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    /**
     * Sets user repository
     * @param userRepository the user repository
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method generates a random recipe based on items in a refrigerator.
     *
     * @param refrigeratorId the id of the refrigerator to generate a recipe from.
     * @return A String containing the generated recipe.
     */
    public String generateRecipe(int refrigeratorId) {
        try {

            List<ItemDto> ingredients = refrigeratorServices.getItemsInRefrigerator(refrigeratorId);

            StringBuilder query = new StringBuilder("A recipe that includes the ingredients ");

            for (ItemDto ingredient : ingredients) {
                query.append(ingredient.getName()).append(" ");
            }

            return aiServices.getChatCompletion(query.toString());
        } catch (IllegalArgumentException e){
            _logger.error("Failed to generate recipe", e);
            return null;
        }
    }


    /**
     * This method generates a weekly menu
     *
     * @param userEmail the email of the user.
     * @param numPeople the number of people to generate the weekly menu for.
     * @return A String containing the generated recipe.
     */
    public String generateWeeklyMenu(String userEmail, int numPeople) {
        try {

            String query = "I need a weekly menu (7 days) for " + numPeople + " people. " +
                    "It should be structured like this: " +
                    "'Monday: dish name', then a list of ingredients," +
                    "then the directions. Add a combined shopping list at the end. Keep the recipes basic.";

            query += " I have some ingredients in my fridge that I would like to use up: ";

            List<ItemDto> ingredients = refrigeratorServices.getItemsInRefrigerator(1);


            for (ItemDto ingredient : ingredients) {
                query += ingredient.getName() + ", ";
            }

            query += ". Do not include any of these ingredients more than in one day/meal. Use metric measurements.";



            String menu = aiServices.getChatCompletion(query);

            saveWeeklyMenu(userEmail, menu);
            // TODO Fill a shopping list

            return menu;
        } catch (IllegalArgumentException e){
            _logger.error("Failed to generate weekly menu", e);
            return null;
        }
    }

    /**
     * Saves a weekly menu
     * @param userEmail the email of the user
     * @param menu the updated weekly menu
     */
    private void saveWeeklyMenu(String userEmail, String menu) {
        try {

            Optional<WeeklyMenu> weeklyMenu = weeklyMenuRepository.findByUserEmail(userEmail);

            WeeklyMenu _weeklyMenu;
            if (weeklyMenu.isPresent()) {
                _weeklyMenu = weeklyMenu.get();
                _weeklyMenu.setMenu(menu);
            } else {
                _weeklyMenu = WeeklyMenu.builder()
                        .user(userRepository.findByEmail(userEmail).get())
                        .build();
            }
            _weeklyMenu.setMenu(menu);
            weeklyMenuRepository.save(_weeklyMenu);
        } catch (IllegalArgumentException e){
            _logger.error("Could not save weekly menu", e);
        }
    }

    /**
     * Gets a user's saved weekly menu
     * @param userEmail the email of the user
     * @return the saved weekly menu
     */
    public String getWeeklyMenu(String userEmail) {
        try {
            Optional<WeeklyMenu> weeklyMenu = weeklyMenuRepository.findByUserEmail(userEmail);
            return weeklyMenu.map(WeeklyMenu::getMenu).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
