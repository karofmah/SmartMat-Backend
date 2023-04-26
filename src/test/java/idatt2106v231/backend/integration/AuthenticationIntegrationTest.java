package idatt2106v231.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.controller.AuthenticationController;
import idatt2106v231.backend.dto.user.UserAuthenticationDto;
import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.service.AuthenticationServices;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test1",
        "spring.datasource.url=jdbc:h2:mem:test1;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationController authenticationController;

    @Autowired
    AuthenticationServices authenticationServices;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup() {

        var user1 = User.builder()
                .email("test1@ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("firstName1")
                .lastName("lastName1")
                .phoneNumber(1234)
                .age(11)
                .household(1)
                .role(Role.USER)
                .build();
        userRepository.save(user1);
    }


    @Nested
    class TestAuthenticatingUsers{

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Logging in a user with correct credentials")
        public void authenticateUserWithCorrectCredentials() throws Exception {

            UserAuthenticationDto testUserCorrectPassword = new UserAuthenticationDto();
            testUserCorrectPassword.setEmail("test1@ntnu.no");
            testUserCorrectPassword.setPassword("password");

            String userJson = objectMapper.writeValueAsString(testUserCorrectPassword);

            mockMvc.perform(post("http://localhost:8080/api/v1/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Logging in a user with wrong credentials")
        public void authenticateUserWithWrongCredentials() throws Exception {

            UserAuthenticationDto testUserCorrectPassword = new UserAuthenticationDto();
            testUserCorrectPassword.setEmail("test1@ntnu.no");
            testUserCorrectPassword.setPassword("wrongPassword");

            String userJson = objectMapper.writeValueAsString(testUserCorrectPassword);

             mockMvc.perform(post("http://localhost:8080/api/v1/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    @Nested
    class TestRegisteringUser {

        @Test
        @DisplayName("Test registering a new user")
        public void registerNewUser() throws Exception {
            UserCreationDto testUser = new UserCreationDto();
            testUser.setEmail("newUser@mail.com");
            testUser.setPassword("password");
            testUser.setAge(1);
            testUser.setFirstName("firstname");
            testUser.setLastName("lastname");
            testUser.setHousehold(1);
            testUser.setPhoneNumber(1234);

            String userJson = objectMapper.writeValueAsString(testUser);

            mockMvc.perform(post("http://localhost:8080/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson))
                    .andExpect(status().isCreated())
                    .andReturn();
        }

        @Test
        @DisplayName("Test registering an existing user")
        public void registerExistingUser() throws Exception {
            UserCreationDto testUser = new UserCreationDto();
            testUser.setEmail("test1@ntnu.no");
            testUser.setPassword("password");
            testUser.setAge(1);
            testUser.setFirstName("firstname");
            testUser.setLastName("lastname");
            testUser.setHousehold(1);
            testUser.setPhoneNumber(1234);

            String userJson = objectMapper.writeValueAsString(testUser);

             mockMvc.perform(post("http://localhost:8080/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isImUsed())
                    .andReturn();
        }

        @Test
        @DisplayName("Register user with one or more fields missing")
        public void registerIncompleteUser() throws Exception {
            UserCreationDto testUser = new UserCreationDto();
            testUser.setEmail("test1@ntnu.no");
            testUser.setAge(1);
            testUser.setFirstName("firstname");
            testUser.setLastName("lastname");
            testUser.setHousehold(1);
            testUser.setPhoneNumber(1234);

            String userJson = objectMapper.writeValueAsString(testUser);

            mockMvc.perform(post("http://localhost:8080/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }
}
