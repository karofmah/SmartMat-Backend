package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.repository.WeekMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Class to manage Recipe objects.
 */
@Service
public class RecipeServices {

    private final WeekMenuRepository weekMenuRepo;
    private final UserRepository userRepo;

    private final AiServices aiServices;
    private final RefrigeratorServices refServices;

    /**
     * Constructor which sets the repositories to use for database access, and services.
     */
    @Autowired
    public RecipeServices(AiServices aiServices, RefrigeratorServices refServices, WeekMenuRepository weekMenuRepo,
                          UserRepository userRepo) {
        this.aiServices = aiServices;
        this.refServices = refServices;
        this.weekMenuRepo = weekMenuRepo;
        this.userRepo = userRepo;
    }

    /**
     * This method generates a random recipe based on items in a refrigerator.
     *
     * @param refrigeratorId the id of the refrigerator to generate a recipe from.
     * @return A String containing the generated recipe.
     */
    public String generateRecipe(int refrigeratorId) {
        try {

            List<ItemInRefrigeratorDto> ingredients = refServices
                    .getTopItemsInRefrigeratorByExpirationDate(refrigeratorId, 5);

            List<ItemInRefrigeratorDto> otherIngredients = refServices
                    .getItemsInRefrigerator(refrigeratorId);


            Random rand = new Random();
            while (ingredients.size() < 5 && !otherIngredients.isEmpty()) {
                int r = rand.nextInt(otherIngredients.size());
                ingredients.add(otherIngredients.get(r));
                otherIngredients.remove(r);
            }

            StringBuilder query = new StringBuilder("A recipe that includes the ingredients ");

            for (ItemInRefrigeratorDto ingredient : ingredients) {
                query.append(ingredient.getItem().getName()).append(" ,");
            }

            query.append("Use metric measurements.");

            return aiServices.getChatCompletion(query.toString());
        } catch (Exception e){
            return "ERROR: " + e.getMessage();
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
            StringBuilder query = new StringBuilder("I need a weekly menu (7 days) for " + numPeople + " people. " +
                    "It should be structured like this: " +
                    "'Monday: dish name', then a list of ingredients," +
                    "then the directions. Add a combined shopping list at the end. Keep the recipes basic. " +
                    "I have some ingredients in my fridge that I would like to use up: ");


            int refrigeratorId = refServices
                    .getRefrigeratorByUserEmail(userEmail)
                    .getRefrigeratorId();

            List<ItemInRefrigeratorDto> ingredients = refServices
                    .getTopItemsInRefrigeratorByExpirationDate(refrigeratorId, 7);

            List<ItemInRefrigeratorDto> otherIngredients = refServices
                    .getItemsInRefrigerator(refrigeratorId);


            Random rand = new Random();
            while (ingredients.size() < 7 && !otherIngredients.isEmpty()) {
                int r = rand.nextInt(otherIngredients.size());
                ingredients.add(otherIngredients.get(r));
                otherIngredients.remove(r);
            }

            for (ItemInRefrigeratorDto ingredient : ingredients) {
                query.append(ingredient.getItem().getName()).append(", ");
            }

            query.append("Do not include any of these ingredients more than in one day/meal. Use metric measurements.");

            String menu = aiServices.getChatCompletion(query.toString());
            if (menu.startsWith("ERROR: ")) throw new Exception();

            saveWeeklyMenu(userEmail, menu);

            return menu;
        } catch (Exception e){
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Saves a weekly menu
     *
     * @param userEmail the email of the user
     * @param menu the updated weekly menu
     * @return if the weekly menu is saved
     */
    public boolean saveWeeklyMenu(String userEmail, String menu) {
        try {
            Optional<WeeklyMenu> existingWeeklyMenu = weekMenuRepo.findByUserEmail(userEmail);

            WeeklyMenu weeklyMenu = existingWeeklyMenu.orElseGet(() -> WeeklyMenu.builder()
                    .user(userRepo.findByEmail(userEmail).get())
                    .build());

            weeklyMenu.setMenu(menu);
            weekMenuRepo.save(weeklyMenu);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    /**
     * Gets a user's saved weekly menu
     *
     * @param userEmail the email of the user
     * @return the saved weekly menu
     */
    public String getWeeklyMenu(String userEmail) {
        try {
            Optional<WeeklyMenu> weeklyMenu = weekMenuRepo.findByUserEmail(userEmail);
            return weeklyMenu.map(WeeklyMenu::getMenu).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}