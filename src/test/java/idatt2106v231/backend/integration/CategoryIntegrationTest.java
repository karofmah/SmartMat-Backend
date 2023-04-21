package idatt2106v231.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.model.Role;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    @DisplayName("Setting up mock data for tests")
    public void setup() {

        categoryRepository.deleteAll();

        User user1=new User("test@ntnu.no","123",
                "First name",
                "Last mame",
                21948391,
                20,
                4,
                Role.USER);

        User user2=new User("test2@ntnu.no",
                "123",
                "First name 2",
                "Last name 2",
                21948391,
                20,
                4,
                Role.USER);
        User user3=new User("test3@ntnu.no",
                "123",
                "First name 3",
                "Last name 3",
                21948391,
                20,
                4,
                Role.USER);

        categoryRepository.save(user1);
        categoryRepository.save(user2);
        categoryRepository.save(user3);
    }

    @DisplayName("Teardown of userRepository")
    @AfterEach
    public void teardown(){
        categoryRepository.deleteAll();
    }


}
