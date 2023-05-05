package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
import idatt2106v231.backend.enums.Role;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class, properties = {
        "spring.config.name=test7",
        "spring.datasource.url=jdbc:h2:mem:test7;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

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

    @BeforeAll
    @Transactional
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
                .user(user1)
                .pinCode(1234)
                .build();

        var subUser2 = SubUser.builder()
                .subUserId(2)
                .accessLevel(false)
                .name("subUser2Name")
                .user(user1)
                .build();

        var subUser3 = SubUser.builder()
                .subUserId(3)
                .accessLevel(false)
                .name("subUser3Name")
                .user(user2)
                .build();

        var subUser4 = SubUser.builder()
                .subUserId(4)
                .accessLevel(true)
                .name("subUser4Name")
                .user(user1)
                .pinCode(1234)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        subUserRepository.save(subUser1);
        subUserRepository.save(subUser2);
        subUserRepository.save(subUser3);
        subUserRepository.save(subUser4);

    }


    @Nested
    class GetSubUsersFromMaster {

        @Test
        @WithMockUser("USER")
        @DisplayName("Test that the correct number of sub users are retrieved")
        public void getSubUsersFromMasterIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/subusers/getUsersFromMaster?email=test1@ntnu.no"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<SubUserDto> retrievedSubUsers = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals(3, retrievedSubUsers.size());
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test retrieving users from a master user that does not exist")
        public void getSubUsersFromMasterIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/subusers/getUsersFromMaster?email=invalidEmail"))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Master user not found",responseString);
        }
    }

    @Nested
    class GetMasterUser {

        @Test
        @WithMockUser("USER")
        @DisplayName("Test retrieval of master user given the sub user")
        public void getMasterUserIsOk() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/subusers/getUser/1"))
                    .andExpect(status().isOk())
                    .andReturn();

            SubUserDto retrievedSubUser = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertEquals("subUser1Name", retrievedSubUser.getName());
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test retrieval of a master user that does not exist")
        public void getMasterUserIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/subusers/getUser/13"))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Sub user not found",responseString);
        }
    }

    @Nested
    class AddSubUser {

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding sub user to database")
        public void addSubUserIsCreated() throws Exception {
            SubUserCreationDto testSubUser = new SubUserCreationDto();
            testSubUser.setUserEmail("test1@ntnu.no");
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

            MvcResult result = mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertTrue(subUserRepository.findByUserEmailAndName("test1@ntnu.no","testSubUser").isPresent());
            Assertions.assertEquals("Sub user saved successfully",responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test adding sub user to database when master user doesnt exist")
        public void addSubUserIsNotFound() throws Exception {
            SubUserCreationDto testSubUser = new SubUserCreationDto();
            testSubUser.setUserEmail("invalidMaster");
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

            MvcResult result = mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Master user not found",responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test adding sub user to database when sub user already exists")
        public void addSubUserIsImUsed() throws Exception {
            SubUserCreationDto testSubUser = new SubUserCreationDto();
            testSubUser.setUserEmail("test1@ntnu.no");
            testSubUser.setName("subUser1Name");
            testSubUser.setAccessLevel(true);
            testSubUser.setPinCode(1234);


            String userJson = objectMapper.writeValueAsString(testSubUser);

           MvcResult result = mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isImUsed())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Sub user already exists",responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test adding sub user to database when master user is undefined")
        public void addSubUserMasterUserIsBadRequest() throws Exception {
            SubUserCreationDto testSubUser = new SubUserCreationDto();
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

            MvcResult result = mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Data is not valid",responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test adding sub user to database when sub user name is undefined")
        public void addSubUserSubUserIsBadRequest() throws Exception {
            SubUserCreationDto testSubUser = new SubUserCreationDto();
            testSubUser.setUserEmail("test1@ntnu.no");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

            MvcResult result = mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Data is not valid",responseString);
        }
    }

    @Nested
    class DeleteSubUser {

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test deletion of sub user")
        public void deleteSubUserIsOk() throws Exception {

            MvcResult result = mockMvc.perform(delete("/api/subusers/deleteSubUser/4")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Sub user deleted",responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test deletion of a sub user that does not exist")
        public void deleteSubUserIsNotFound() throws Exception {

            MvcResult result = mockMvc.perform(delete("/api/subusers/deleteSubUser/30")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Sub user not found",responseString);
        }
    }

    @Nested
    class ValidatePinCode {

        @Test
        @WithMockUser("USER")
        @DisplayName("Tests validation of pin code when pin code is correct")
        public void validatePinCodeIsOk() throws Exception {
            SubUserValidationDto subUserValidationDto = SubUserValidationDto.builder()
                    .subUserId(1)
                    .pinCode(1234)
                    .build();

            String subUserDtoJson = objectMapper.writeValueAsString(subUserValidationDto);

            MvcResult result = mockMvc.perform(post("http://localhost:8080/api/subusers/validatePinCode")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subUserDtoJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Pin code is correct", responseString);
        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Tests validation of pin code when pin code is incorrect")
        public void validatePinCodeIsNotFound() throws Exception {
            SubUserValidationDto subUserValidationDto = SubUserValidationDto.builder()
                    .subUserId(1)
                    .pinCode(2345)
                    .build();

            String subUserDtoJson = objectMapper.writeValueAsString(subUserValidationDto);

            MvcResult result = mockMvc.perform(post("http://localhost:8080/api/subusers/validatePinCode")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subUserDtoJson))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Pin code is incorrect", responseString);
        }
    }
}