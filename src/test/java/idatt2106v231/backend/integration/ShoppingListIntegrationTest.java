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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    SubUserRepository subUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RefrigeratorRepository refrigeratorRepository;

    @Autowired
    ItemRefrigeratorRepository itemRefrigeratorRepository;

    @BeforeAll
    @Transactional
    @DisplayName("Populating the database with testdata")
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

        var user3 = User.builder()
                .email("test3@ntnu.no")
                .password(passwordEncoder.encode("password"))
                .age(3)
                .firstName("firstName3")
                .lastName("lastName3")
                .phoneNumber(3456)
                .age(33)
                .household(3)
                .role(Role.USER)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        var subUser1 = SubUser.builder()
                .user(user1)
                .accessLevel(true)
                .name("Subuser1")
                .build();

        var subUser2 = SubUser.builder()
                .user(user1)
                .accessLevel(false)
                .name("Subuser2")
                .build();

        var subUser3 = SubUser.builder()
                .user(user2)
                .accessLevel(true)
                .name("Subuser3")
                .build();

        var subUser4 = SubUser.builder()
                .user(user3)
                .accessLevel(true)
                .name("Subuser4")
                .build();

        subUserRepository.save(subUser1);
        subUserRepository.save(subUser2);
        subUserRepository.save(subUser3);
        subUserRepository.save(subUser4);

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

        var shoppingListUser3 = ShoppingList.builder()
                .user(user3)
                .build();

        shoppingListRepository.save(shoppingListUser1);
        shoppingListRepository.save(shoppingListUser2);
        shoppingListRepository.save(shoppingListUser3);

        var itemShoppingList1 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.KG)
                .shoppingList(shoppingListUser1)
                .item(item1)
                .subUser(subUser1)
                .build();

        var itemShoppingList2 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.L)
                .shoppingList(shoppingListUser1)
                .item(item2)
                .subUser(subUser2)
                .build();

        var itemShoppingList3 = ItemShoppingList.builder()
                .amount(300)
                .measurementType(Measurement.G)
                .shoppingList(shoppingListUser1)
                .item(item3)
                .subUser(subUser1)
                .build();

        var itemShoppingListUser2 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.L)
                .shoppingList(shoppingListUser2)
                .item(item2)
                .subUser(subUser2)
                .build();

        var itemShoppingListUser3 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.L)
                .shoppingList(shoppingListUser3)
                .item(item2)
                .subUser(subUser4)
                .build();


        itemShoppingListRepository.save(itemShoppingList1);
        itemShoppingListRepository.save(itemShoppingList2);
        itemShoppingListRepository.save(itemShoppingList3);
        itemShoppingListRepository.save(itemShoppingListUser2);
        itemShoppingListRepository.save(itemShoppingListUser3);


        var refrigerator1 = Refrigerator.builder()
                .refrigeratorId(1)
                .user(user1)
                .build();

        refrigeratorRepository.save(refrigerator1);


        var itemRefrigerator1 = ItemRefrigerator.builder()
                .item(item1)
                .refrigerator(refrigerator1)
                .measurementType(Measurement.KG)
                .build();

        itemRefrigeratorRepository.save(itemRefrigerator1);
    }

    @Nested
    class GetItemsFromShoppingList {

        @Test
        @WithMockUser("USER")
        @DisplayName("Test retrieval of items from shopping list")
        public void getItemsFromShoppingListIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/shoppingList/getItemsFromShoppingList")
                    .param("email","test1@ntnu.no"))
                    .andExpect(status().isOk())
                    .andReturn();

            ShoppingListDto shoppinglist = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(3, shoppinglist.getItems().size());
            assertEquals(1, shoppinglist.getItems().get(0).getItemShoppingListId());
            assertTrue(shoppinglist.getItems().get(0).isSubUserAccessLevel());

        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test retrieval of items from shopping list of an invalid user")
        public void getItemsFromShoppingListIsBadRequest() throws Exception {
             MvcResult result = mockMvc.perform(get("/api/shoppingList/getItemsFromShoppingList")
                            .param("email","invalidUser"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("User not found",responseString);
        }
    }

    @Nested
    class AddItemToShoppingList {

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding item to shopping list")
        public void addItemToShoppingListIsCreated() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(2)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .subUserId(1)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            int size=itemShoppingListRepository.findAll().size();
             MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(shoppingListJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

             assertEquals("Item saved to shopping list",responseString);
             assertEquals(size+1,itemShoppingListRepository.findAll().size());

        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding an existing item to shopping list ")
        public void addItemToShoppingListIsOk() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Cheese")
                    .amount(1.5)
                    .measurementType(Measurement.G)
                    .subUserId(1)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

             MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            assertEquals("Updated amount of the item",responseString);
            Optional<ItemShoppingList> item = itemShoppingListRepository.findById(1);
            Assertions.assertEquals(1.5, item.get().getAmount());
        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding an invalid item to shopping list")
        public void addItemToShoppingListIsBadRequest() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("invalidItem")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .subUserId(1)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            assertEquals("Item doesnt exist",responseString);
        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding an item to shopping list when amount of item is invalid")
        public void addItemToShoppingListAmountIsBadRequest() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Milk")
                    .amount(0)
                    .measurementType(Measurement.KG)
                    .subUserId(1)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            assertEquals("Invalid amount",responseString);
        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test adding an item to shopping list when measurement is invalid")
        public void addItemToShoppingListMeasurementIsBadRequest() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(1)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(null)
                    .subUserId(1)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(post("/api/shoppingList/addItemToShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            assertEquals("Measurement is not specified",responseString);
        }
    }

    @Nested
    class DeleteItemFromShoppingList {

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test deletion of item from shopping list")
        public void deleteItemFromShoppingListOk() throws Exception {
            var itemInShoppingListCreationDto = ItemInShoppingListCreationDto.builder()
                    .shoppingListId(2)
                    .itemShoppingListId(2)
                    .itemName("Milk")
                    .amount(1)
                    .measurementType(Measurement.L)
                    .subUserId(2)
                    .build();

            String shoppingListJson = objectMapper.writeValueAsString(itemInShoppingListCreationDto);

            MvcResult result = mockMvc.perform(delete("/api/shoppingList/deleteItemFromShoppingList")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shoppingListJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            assertEquals("Item was updated",responseString);
        }
    }

    @Nested
    class AddMostPopularItemsToShoppingList {

        @Test
        @WithMockUser("USER")
        @DisplayName("Test adding most popular items to shopping list")
        public void addMostPopularItemsToShoppingListIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/shoppingList/addMostPopularItems")
                            .param("shoppingListId","1")
                            .param("subUserId","1"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            assertEquals("Successfully added popular items that are not already in the shopping list"
                    , responseString);
        }
    }
}