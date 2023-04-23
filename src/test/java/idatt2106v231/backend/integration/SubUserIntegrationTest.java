package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.model.Role;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.SubUserRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestPropertySource(locations = "classpath:application-william.properties")

public class SubUserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubUserRepository subUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    @DisplayName("Populating the database with testdata")
    public void setup() {

        var user1 = User.builder()
                .email("test1@ntnu.no")
                .password(passwordEncoder.encode("password"))
                .age(1)
                .firstName("firstName1")
                .lastName("lastName1")
                .phoneNumber(1234)
                .age(11)
                .household(1)
                .role(Role.USER)
                .build();

        var user2 = User.builder()
                .email("test2@ntnu.no")
                .password(passwordEncoder.encode("password"))
                .age(2)
                .firstName("firstName2")
                .lastName("lastName2")
                .phoneNumber(2345)
                .age(22)
                .household(2)
                .role(Role.USER)
                .build();

        var subUser1 = SubUser.builder()
                .subUserId(1)
                .accessLevel(true)
                .name("subUser1Name")
                .masterUser(user1)
                .build();

        var subUser2 = SubUser.builder()
                .subUserId(2)
                .accessLevel(false)
                .name("subUser2Name")
                .masterUser(user1)
                .build();

        var subUser3 = SubUser.builder()
                .subUserId(3)
                .accessLevel(false)
                .name("subUser3Name")
                .masterUser(user2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        subUserRepository.save(subUser1);
        subUserRepository.save(subUser2);
        subUserRepository.save(subUser3);
    }

    @AfterEach
    @DisplayName("Teardown of user table and subuser table")
    public void teardown() {
        userRepository.deleteAll();
        subUserRepository.deleteAll();
    }

    @Nested
    class TestGetUsersFromMaster {

        @Test
        @DisplayName("Method retrieves the correct number of subusers")
        public void retrieveSubUsersFromMaster() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/subusers/getUsersFromMaster")
                    .param("email","test1@ntnu.no"))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("hei");
            List<SubUserDto> retrievedSubUsers = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<SubUserDto>>() {});
            assertEquals(2, retrievedSubUsers.size());
        }
    }
}
