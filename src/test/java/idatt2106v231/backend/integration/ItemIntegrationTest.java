package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.repository.CategoryRepository;
import idatt2106v231.backend.repository.ItemRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ItemIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    @Transactional
    @DisplayName("Add test data to test database")
    public void setup() {

        Category category = Category.builder().description("category").build();

        categoryRepository.save(category);

        System.out.println(category.getCategoryId());
        Item item1 = Item.builder().name("test1").category(category).build();
        Item item2 = Item.builder().name("test2").category(category).build();
        Item item3 = Item.builder().name("test3").category(category).build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }


    @Nested
    class GetItems {

        @Test
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all items")
        public void getItemsIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/items/getAllItems")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            List<ItemDto> actualItems = mapper.readValue(responseString, new TypeReference<>() {
            });


            System.out.println(actualItems);
            Assertions.assertEquals(itemRepository.findAll().size(), actualItems.size());

        }

        @Test
        @Transactional
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all items when no items are in database")
        public void getItemsIsNotFound() throws Exception {

            itemRepository.deleteAll();
            MvcResult result = mockMvc.perform(get("/api/items/getAllItems")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("There are no items registered in the database", responseString);

        }
    }

    @Nested
    class GetItem {
        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting an item that exists in database")

        public void getItemIsOk() throws Exception {


            MvcResult result = mockMvc.perform(get("/api/items/getItem/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ItemDto retrievedItem = objectMapper.readValue(responseString, new TypeReference<>() {
            });
            System.out.println("Item: " + retrievedItem);
            Assertions.assertEquals("test1", retrievedItem.getName());


        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting an item that does not exist in database")
        public void getItemIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/items/getItem/4")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            System.out.println("Response: " + responseString);
            Assertions.assertEquals("Item does not exist", responseString);


        }

    }


    @Nested
    class SaveItem {
        @Test
        @Transactional
        @DisplayName("Testing the endpoint for saving an Item to database")
        public void saveItemIsCreated() throws Exception {

            ItemDto newItemDto = ItemDto.builder().name("newTest").categoryId(1).build();


            String newItemJson = objectMapper.writeValueAsString(newItemDto);

            MvcResult result = mockMvc.perform(post("/api/items/saveItem")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newItemJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<Item> itemOptional = itemRepository.findByName(newItemDto.getName());
            Assertions.assertTrue(itemOptional.isPresent());
            Item retrievedItem = itemOptional.get();

            Assertions.assertEquals("Item saved to database", responseString);
            Assertions.assertEquals(newItemDto.getName(), retrievedItem.getName());

        }

        @Test
        @DisplayName("Testing the endpoint for saving an item to database when it already exists")
        public void saveItemIsImUsed() throws Exception {

            ItemDto existingItemDto = ItemDto.builder().name("test1").categoryId(1).build();


            String existingItemJson = objectMapper.writeValueAsString(existingItemDto);

            MvcResult result = mockMvc.perform(post("/api/items/saveItem")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingItemJson))
                    .andExpect(status().isImUsed())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Item already exists", responseString);

        }

        @Test
        @DisplayName("Testing the endpoint for saving an item of a category that does not exist")
        public void saveItemIsNotFound() throws Exception {

            ItemDto existingItemDto = ItemDto.builder().name("test4").categoryId(10).build();


            String existingItemJson = objectMapper.writeValueAsString(existingItemDto);

            MvcResult result = mockMvc.perform(post("/api/items/saveItem")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingItemJson))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Category does not exist", responseString);

        }
        @Test
        @DisplayName("Testing the endpoint for saving an item of a category that does not exist")
        public void saveItemIsBadRequest() throws Exception {

            ItemDto existingItemDto = ItemDto.builder().name("").build();


            String existingItemJson = objectMapper.writeValueAsString(existingItemDto);

            MvcResult result = mockMvc.perform(post("/api/items/saveItem")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingItemJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Data is not specified", responseString);

        }
    }
    @Nested
    class DeleteItem{
        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test deletion of item")
        public void deleteItemIsOk() throws Exception {

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/items/deleteItem/3")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(2,itemRepository.findAll().size());
            Assertions.assertEquals("Item removed from database",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test deletion of item when item is not found in database")
        public void deleteItemIsNotFound() throws Exception {

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/items/deleteItem/4")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("Item does not exist",responseString);


        }
    }
}