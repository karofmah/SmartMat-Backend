package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.subuser.SubUserDto;
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
                .masterUser(user1)
                .pinCode(1234)
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

        var subUser4 = SubUser.builder()
                .subUserId(4)
                .accessLevel(true)
                .name("subUser4Name")
                .masterUser(user1)
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
    class GetUsersFromMaster {

        @Test
        @DisplayName("Retrieves the correct number of subusers")
        public void retrieveSubUsersFromMaster() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/subusers/getUsersFromMaster")
                    .param("email","test1@ntnu.no"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<SubUserDto> retrievedSubUsers = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(3, retrievedSubUsers.size());
        }

        @Test
        @DisplayName("Returns error when wrong master is given")
        public void returnErrorWhenGivenWrongParam() throws Exception {
            mockMvc.perform(get("/api/subusers/getUsersFromMaster")
                    .param("email", "invalidEmail"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }
    }

    @Nested
    class GetUser {

        @Test
        @DisplayName("Returns correct user")
        public void returnCorrectUser() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/subusers/getUser/1"))
                    .andExpect(status().isOk())
                    .andReturn();

            SubUserDto retrievedSubUser = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals("subUser1Name", retrievedSubUser.getName());
            assertEquals("test1@ntnu.no", retrievedSubUser.getMasterUserEmail());
        }

        @Test
        @DisplayName("Returns incorrect user")
        public void returnIncorrectUser() throws Exception {
            mockMvc.perform(get("/api/subusers/getUser/13"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

    }

    @Nested
    class AddSubUser {

        @Test
        @DisplayName("Returns ok when requirements are met")
        public void addSubUserAllArgsOk() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("test1@ntnu.no");
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when masteruser doesnt exist")
        public void addSubUserMasterDoesntExist() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("invalidMaster");
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when subuser already exist")
        public void addSubUserSubUserExists() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("test1@ntnu.no");
            testSubUser.setName("subUser1Name");
            testSubUser.setAccessLevel(true);
            testSubUser.setPinCode(1234);


            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isImUsed())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when masteruser is undefined")
        public void addSubUserMasterUndefined() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setName("testSubUser");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when name is undefined")
        public void addSubUserNameUndefined() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("test1@ntnu.no");
            testSubUser.setAccessLevel(false);

            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when accesslevel is undefined")
        public void addSubUserAccesslevelUndefined() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("test1@ntnu.no");
            testSubUser.setName("testSubUser");


            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(post("/api/subusers/addSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    class DeleteUser {

        @Test
        @DisplayName("Returns ok when requirements are met")
        public void deleteSubUserAllArgsOk() throws Exception {

             mockMvc.perform(delete("/api/subusers/deleteSubUser/4")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when subuser doesnt exist")
        public void deleteSubUserDoesntExist() throws Exception {
            SubUserDto testSubUser = new SubUserDto();
            testSubUser.setMasterUserEmail("invalidMaster");
            testSubUser.setName("invalidName");

            String userJson = objectMapper.writeValueAsString(testSubUser);

             mockMvc.perform(delete("/api/subusers/deleteSubUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }
    }

    @Nested
    class ValidatePinCode{

        @Test
        @DisplayName("Tests validation of pin code when pin code is correct")
        public void validatePinCodeIsOk() throws Exception {

            SubUserDto subUserDto=SubUserDto.builder().masterUserEmail("test1@ntnu.no").name("subUser1Name").pinCode(1234).accessLevel(true).build();

            String subUserDtoJson = objectMapper.writeValueAsString(subUserDto);

            MvcResult result= mockMvc.perform(post("http://localhost:8080/api/subusers/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subUserDtoJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Pin code is correct",responseString);
        }
        @Test
        @DisplayName("Tests validation of pin code when pin code is incorrect")
        public void validatePinCodeIsNotFound() throws Exception {

            SubUserDto subUserDto=SubUserDto.builder().masterUserEmail("test1@ntnu.no").name("subUser1Name").pinCode(123).accessLevel(true).build();

            String subUserDtoJson = objectMapper.writeValueAsString(subUserDto);


            MvcResult result= mockMvc.perform(post("http://localhost:8080/api/subusers/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subUserDtoJson))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Pin code is incorrect",responseString);
        }
        @Test
        @DisplayName("Tests validation of pin code when pin code is not specified")
        public void validatePinCodeIsBadRequest() throws Exception {

            SubUserDto subUserDto=SubUserDto.builder().masterUserEmail("test1@ntnu.no").name("subUser1Name").accessLevel(true).build();

            String subUserDtoJson = objectMapper.writeValueAsString(subUserDto);


            MvcResult result= mockMvc.perform(post("http://localhost:8080/api/subusers/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(subUserDtoJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Data is not valid",responseString);
        }
    }
    }
