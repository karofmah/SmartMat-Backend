package idatt2106v231.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.repository.WeekMenuRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.ParseException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test9",
        "spring.datasource.url=jdbc:h2:mem:test9;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",

})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WeekMenuRepository weekMenuRepo;

    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup() throws ParseException {

        User user1 = User.builder().
                email("test1@ntnu.no")
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber(98765432)
                .age(21)
                .password("12345678")
                .household(1)
                .build();

        userRepo.save(user1);

        WeeklyMenu weeklyMenu1 = WeeklyMenu.builder()
                .menu("Test menu")
                .user(user1)
                .build();

        weekMenuRepo.save(weeklyMenu1);
    }

    @Nested
    class GenerateRecipe {
        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test generating a recipe when refrigerator does not exist in database")
        public void generateRecipeRefrigeratorNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/recipes/generateRecipe?refrigeratorId=999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Refrigerator does not exist",responseString);
        }
    }

    @Nested
    class GetWeeklyMenu {
        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting a weekly menu that exists in database")
        public void getWeeklyMenuIsOk() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/recipes/getWeeklyMenu/test1@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Test menu",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting a weekly menu that does not exist in database")
        public void getWeeklyMenuIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/recipes/getWeeklyMenu/notExisting@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("User does not exist", responseString);
        }
    }

    @Nested
    class GenerateWeeklyMenu {
        @Test
        @WithMockUser("USER")
        @DisplayName("Test generating weekly menu with a user that does not exist")
        public void generateWeeklyMenuUserIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/recipes/generateWeeklyMenu/notExisting@ntnu.no?numPeople=1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("User does not exist", responseString);
        }
        @Test
        @WithMockUser("USER")
        @DisplayName("Test generating weekly menu with invalid number of people")
        public void generateWeeklyMenuIsBadRequest() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/recipes/generateWeeklyMenu/test1@ntnu.no?numPeople=-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Number of people must be at least 1", responseString);
        }
    }
}