package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;




    @BeforeAll
    @DisplayName("Setting up mock data for tests")
    public void setup() {

        User user1=new User();

        user1.setEmail("test@ntnu.no");
        user1.setFirstName("First name");
        user1.setLastName("Last name");
        user1.setPhoneNumber(29185929);
        user1.setAge(20);
        user1.setPassword("123");
        user1.setHousehold(4);



        userRepository.save(user1);

    }



    @Nested
    class TestGetUser{

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting valid user")
        public void getValidUser() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/users/login/getUser?email=test@ntnu.no")
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
            MvcResult result = mockMvc.perform(get("/api/users/login/getUser?email=invalid@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("User not found",responseString);

        }
    }
}
