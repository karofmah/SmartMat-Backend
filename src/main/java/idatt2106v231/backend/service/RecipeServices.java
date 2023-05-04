package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.repository.WeekMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to manage Recipe objects.
 */
@Service
public class RecipeServices {

    private final AiServices aiServices;
    private final RefrigeratorServices refrigeratorServices;
    private final WeekMenuRepository weekMenuRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeServices(AiServices aiServices, RefrigeratorServices refrigeratorServices, WeekMenuRepository weekMenuRepository,
                          UserRepository userRepository) {
        this.aiServices = aiServices;
        this.refrigeratorServices = refrigeratorServices;
        this.weekMenuRepository = weekMenuRepository;
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

            List<ItemInRefrigeratorDto> ingredients = new ArrayList<>(
                    refrigeratorServices.getTopItemsInRefrigeratorByExpirationDate(refrigeratorId, 5));

            List<ItemInRefrigeratorDto> otherIngredients = new ArrayList<>(refrigeratorServices.getItemsInRefrigerator(refrigeratorId));

            Random rand = new Random();
            while (ingredients.size() < 5 && !otherIngredients.isEmpty()) {
                int r = rand.nextInt(otherIngredients.size());
                ingredients.add(otherIngredients.get(r));
                otherIngredients.remove(r);
            }
            ingredients.forEach(c -> System.out.println(c.getItem().getName()));

            StringBuilder query = new StringBuilder("A recipe that includes the ingredients ");

            for (ItemInRefrigeratorDto ingredient : ingredients) {
                query.append(ingredient.getItem().getName()).append(" ,");
            }

            query.append("Use metric measurements.");

            return aiServices.getChatCompletion(query.toString());
        } catch (Exception e){
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
            StringBuilder query = new StringBuilder("I need a weekly menu (7 days) for " + numPeople + " people. " +
                    "It should be structured like this: " +
                    "'Monday: dish name', then a list of ingredients," +
                    "then the directions. Add a combined shopping list at the end. Keep the recipes basic. " +
                    "I have some ingredients in my fridge that I would like to use up: ");

            int refrigeratorId = refrigeratorServices.getRefrigeratorByUserEmail(userEmail).getRefrigeratorId();
            List<ItemInRefrigeratorDto> ingredients = new ArrayList<>(
                    refrigeratorServices.getTopItemsInRefrigeratorByExpirationDate(refrigeratorId, 7));

            List<ItemInRefrigeratorDto> otherIngredients = new ArrayList<>(refrigeratorServices.getItemsInRefrigerator(refrigeratorId));

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

            saveWeeklyMenu(userEmail, menu);

            return menu;
        } catch (IllegalArgumentException e){
            return null;
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
            Optional<WeeklyMenu> weeklyMenu = weekMenuRepository.findByUserEmail(userEmail);

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
            weekMenuRepository.save(_weeklyMenu);
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
            Optional<WeeklyMenu> weeklyMenu = weekMenuRepository.findByUserEmail(userEmail);
            return weeklyMenu.map(WeeklyMenu::getMenu).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}