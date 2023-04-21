package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.Role;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.CategoryRepository;
import idatt2106v231.backend.repository.ItemRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CategoryIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    @DisplayName("Setting up mock data for tests")
    public void setup() {

        itemRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category1=new Category();
        category1.setDescription("test");

        categoryRepository.save(category1);

    }

    @DisplayName("Teardown of category repository")
    @AfterEach
    public void teardown(){
        categoryRepository.deleteAll();
    }

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
        List<User> actualUsers = mapper.readValue(responseString, new TypeReference<>() {
        });

        Assertions.assertEquals(categoryRepository.findAll().size(),actualUsers.size());
    }
}

