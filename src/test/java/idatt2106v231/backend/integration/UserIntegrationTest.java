package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test5",
        "spring.datasource.url=jdbc:h2:mem:test5;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",

})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup() {
        var user = User.builder()
                .email("test@ntnu.no")
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber(29185929)
                .age(20)
                .password("123")
                .household(4)
                .build();
        userRepository.save(user);
    }

    @Nested
    class GetUser{

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting valid user")
        public void getUserIsOk() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/users/login/getUser?email=test@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            User retrievedUser= objectMapper.readValue(responseString, new TypeReference<>() {});
            Assertions.assertEquals("test@ntnu.no",retrievedUser.getEmail());
        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting invalid user")
        public void getUserIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/users/login/getUser?email=invalid@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("User does not exist",responseString);
        }
    }

    @Test
    @WithMockUser(username = "USER")
    @DisplayName("Test updating a user")
    public void updateUserIsOk() throws Exception {

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .email("test@ntnu.no")
                .firstName("New first name")
                .lastName("New last name")
                .phoneNumber(11111111)
                .household(10)
                .build();

        String updateUserJson = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult result = mockMvc.perform(put("/api/users/updateUser?email=test@ntnu.no")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        User retrievedUser = userRepository.findByEmail(userUpdateDto.getEmail()).get();

        Assertions.assertEquals("User is updated", responseString);
        Assertions.assertEquals(userUpdateDto.getFirstName(), retrievedUser.getFirstName());
        Assertions.assertEquals(userUpdateDto.getLastName(), retrievedUser.getLastName());
        Assertions.assertEquals(userUpdateDto.getPhoneNumber(), retrievedUser.getPhoneNumber());
        Assertions.assertEquals(userUpdateDto.getHousehold(), retrievedUser.getHousehold());
    }
}