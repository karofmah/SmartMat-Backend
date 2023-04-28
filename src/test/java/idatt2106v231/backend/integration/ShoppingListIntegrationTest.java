package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.ShoppingListDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.*;
import idatt2106v231.backend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class, properties = {
        "spring.config.name=test6",
        "spring.datasource.url=jdbc:h2:mem:test6;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShoppingListIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    ItemShoppingListRepository itemShoppingListRepository;

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

        userRepository.save(user1);
        userRepository.save(user2);

        var category = Category.builder()
                .description("Dairy")
                .build();

        categoryRepository.save(category);

        var item1 = Item.builder()
                .name("Cheese")
                .category(category)
                .build();

        var item2 = Item.builder()
                .name("Milk")
                .category(category)
                .build();

        var item3 = Item.builder()
                .name("Yoghurt")
                .category(category)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        var shoppingListUser1 = ShoppingList.builder()
                .user(user1)
                .build();

        var shoppingListUser2 = ShoppingList.builder()
                .user(user2)
                .build();

        shoppingListRepository.save(shoppingListUser1);
        shoppingListRepository.save(shoppingListUser2);

        var itemShoppingList1 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.KG)
                .shoppingList(shoppingListUser1)
                .item(item1)
                .build();

        var itemShoppingList2 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.L)
                .shoppingList(shoppingListUser1)
                .item(item2)
                .build();

        var itemShoppingList3 = ItemShoppingList.builder()
                .amount(300)
                .measurement(Measurement.G)
                .shoppingList(shoppingListUser1)
                .item(item3)
                .build();

        var itemShoppingListUser2 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.L)
                .shoppingList(shoppingListUser2)
                .item(item2)
                .build();

        itemShoppingListRepository.save(itemShoppingList1);
        itemShoppingListRepository.save(itemShoppingList2);
        itemShoppingListRepository.save(itemShoppingList3);
        itemShoppingListRepository.save(itemShoppingListUser2);
    }

    /*@AfterEach
    @DisplayName("Teardown of database")
    public void teardown() {
        itemShoppingListRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        shoppingListRepository.deleteAll();
        userRepository.deleteAll();
    }*/

    @Nested
    class TestGetItemsFromShoppingList {

        @Test
        @DisplayName("Retrieve correct items from shoppinglist")
        public void retrieveItemsFromShoppingList() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/shoppingList/getItemsFromShoppingList")
                    .param("email","test1@ntnu.no"))
                    .andExpect(status().isOk())
                    .andReturn();

            ShoppingListDto shoppinglist = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(3, shoppinglist.getItems().size());
        }

        @Test
        @DisplayName("Return error when supplied invalid user")
        public void returnErrorWithInvalidUser() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/shoppingList/getItemsFromShoppingList")
                            .param("email","invalidUser"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

        }
    }

    @Nested
    class TestAddItemToShoppingList {

        @Test
        @DisplayName("Return ok when all requirements are met")
        public void addItemAllArgsOk() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(2)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(shoppingListJson))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns conflict when item already exists in the shoppinglist")
        public void addItemAlreadyExists() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Cheese")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isConflict())
                    .andReturn();
        }

        @Test
        @DisplayName("Returns error when item is invalid")
        public void addItemItemIsInvalid() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("invalidItem")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("Return error when amount is invalid")
        public void addItemAmountIsInvalid() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Milk")
                    .amount(0)
                    .measurementType(Measurement.L)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @DisplayName("Return error when measurement is invalid")
        public void addItemMeasurementIsInvalid() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(null)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    class TestDeleteItemFromShoppingList {

        @Test
        @DisplayName("Return ok when all requirements are met")
        public void deleteItemAllArgsOk() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(2)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(delete("/api/shoppingList/deleteItemFromShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }
}