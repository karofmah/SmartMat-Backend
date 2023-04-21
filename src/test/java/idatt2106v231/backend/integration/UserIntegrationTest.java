package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.controller.UserController;
import idatt2106v231.backend.model.Role;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.service.UserServices;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;




    @BeforeEach
    @DisplayName("Setting up mock data for tests")
    public void setup() {

        userRepository.deleteAll();

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

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @DisplayName("Teardown of userRepository")
    @AfterEach
    public void teardown(){
        userRepository.deleteAll();
    }


    @Nested
    class TestGetUsers{

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting valid user")
        public void getValidUser() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/users/login/user?email=test@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            User retrievedUser= objectMapper.readValue(responseString, new TypeReference<>() {
            });
            Assertions.assertEquals("test@ntnu.no",retrievedUser.getEmail());


        }
        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting invalid user")
        public void getInvalidUser() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/users/login/user?email=invalid@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("User not found",responseString);

        }
    }
}
