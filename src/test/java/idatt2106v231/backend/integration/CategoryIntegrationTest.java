package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.item.CategoryDto;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
public class CategoryIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    @DisplayName("Setting up mock data for tests")
    public void setup() {


        Category category1=Category.builder().description("test").build();
        Category category2=Category.builder().description("test2").build();
        Category category3=Category.builder().description("test3").build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }

    @DisplayName("Teardown of category repository")
    @AfterEach
    public void teardown(){
       categoryRepository.deleteAll();
    }


    @Nested
    class TestGetCategories {

        @Test
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all categories")
        public void getCategories() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/categories/getAllCategories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            List<Category> actualCategories = mapper.readValue(responseString, new TypeReference<>() {
            });

            Assertions.assertEquals(categoryRepository.findAll().size(), actualCategories.size());

        }

        @Test
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all categories")
        public void getCategoriesEmpty() throws Exception {

            categoryRepository.deleteAll();
            MvcResult result = mockMvc.perform(get("/api/categories/getAllCategories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("There are no categories registered in the database", responseString);

        }
    }

    @Nested
    class TestSaveCategory{
        @Test
        @DisplayName("Testing the endpoint for saving a category to database")
        public void saveCategory() throws Exception {
            CategoryDto newCategoryDto=CategoryDto.builder().description("newTest").build();

            String newCategoryJson=objectMapper.writeValueAsString(newCategoryDto);

            MvcResult result= mockMvc.perform(post("/api/categories/saveCategory")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newCategoryJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<Category> categoryOptional = categoryRepository.findByDescription(newCategoryDto.getDescription());
            Assertions.assertTrue(categoryOptional.isPresent());
            Category retrievedCategory = categoryOptional.get();

            Assertions.assertEquals("Category is saved to database", responseString);
            Assertions.assertEquals(newCategoryDto.getDescription(), retrievedCategory.getDescription());

        }
        @Test
        @DisplayName("Testing the endpoint for saving a category to database when it already exists")
        public void saveExistingCategory() throws Exception {
            CategoryDto existingCategoryDto=CategoryDto.builder().description("test").build();

            String existingCategoryJson=objectMapper.writeValueAsString(existingCategoryDto);

            MvcResult result= mockMvc.perform(post("/api/categories/saveCategory")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingCategoryJson))
                    .andExpect(status().isImUsed())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Category already exists", responseString);

        }
    }

    //Change endpoint either to find by name or by an alternative solution for id
/*
    @Test
    @WithMockUser(username = "USER")
    @DisplayName("Test getting valid category")

    public void getValidCategory() throws Exception {


        MvcResult result = mockMvc.perform(get("/api/categories/getCategory/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        Category retrievedCategory= objectMapper.readValue(responseString, new TypeReference<>() {
        });
        System.out.println("Category: " + retrievedCategory);
        Assertions.assertEquals("test",retrievedCategory.getDescription());


    }


    //Change endpoint either to find by name or by an alternative solution for id
    @Test
    @WithMockUser(username = "USER")
    @DisplayName("Test getting invalid category")
    public void getInvalidCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/getCategory/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();

        System.out.println("Category: " + responseString);
        Assertions.assertEquals("Category is not registered in the database",responseString);


    }

 */
}

