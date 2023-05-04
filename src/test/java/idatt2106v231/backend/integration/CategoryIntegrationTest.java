package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.item.CategoryDto;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test2",
        "spring.datasource.url=jdbc:h2:mem:test2;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class CategoryIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeAll
    @Transactional
    @DisplayName("Add test data to test database")
    public void setup() {

        var category1 = Category.builder()
                .description("Drinks")
                .build();
        var category2 = Category.builder()
                .description("Dessert")
                .build();
        var category3 = Category.builder()
                .description("Dairy")
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }

    @Nested
    class GetCategories {

        @Test
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all categories")
        public void getCategoriesIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/categories/getAllCategories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            List<CategoryDto> actualCategories = mapper.readValue(responseString, new TypeReference<>() {
            });

            Assertions.assertEquals(categoryRepository.findAll().size(), actualCategories.size());
        }

        @Test
        @WithMockUser(username = "ADMIN")
        @Transactional
        @DisplayName("Testing the endpoint for retrieving categories that does not exist")
        public void getCategoriesIsNotFound() throws Exception {
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
    class SaveCategory{

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Testing the endpoint for saving a category to database")
        public void saveCategoryIsCreated() throws Exception {
            CategoryDto newCategoryDto = CategoryDto.builder()
                    .description("newTest")
                    .build();

            String newCategoryJson = objectMapper.writeValueAsString(newCategoryDto);

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
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Testing the endpoint for saving a category to database when it already exists")
        public void saveCategoryIsImUsed() throws Exception {
            CategoryDto existingCategoryDto = CategoryDto.builder()
                    .description("Drinks")
                    .build();

            String existingCategoryJson=  objectMapper.writeValueAsString(existingCategoryDto);

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


    @Nested
    class GetCategory{

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting valid category")
        public void getCategoryIsOk() throws Exception {
             mockMvc.perform(get("/api/categories/getCategory/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting invalid category")
        public void getCategoryIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/categories/getCategory/30")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Category does not exist",responseString);
        }
    }

    @Nested
    class DeleteCategory{
        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test deletion of category")
        public void deleteCategoryIsOk() throws Exception {
            int size = categoryRepository.findAll().size();

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/categories/deleteCategory/3")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size-1, categoryRepository.findAll().size());
            Assertions.assertEquals("Category removed from database", responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test deletion of category when category is not found in database")
        public void deleteCategoryIsNotFound() throws Exception {
            int size = categoryRepository.findAll().size();

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/categories/deleteCategory/30")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size, categoryRepository.findAll().size());
            Assertions.assertEquals("Category does not exist", responseString);
        }
    }
}