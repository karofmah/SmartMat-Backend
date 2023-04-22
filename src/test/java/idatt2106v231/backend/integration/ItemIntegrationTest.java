package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.repository.CategoryRepository;
import idatt2106v231.backend.repository.ItemRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

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
    @DisplayName("Setting up mock data for tests")
    public void setup() {

        Category category=Category.builder().description("category").build();

        categoryRepository.save(category);

        System.out.println(category);
        Item item1=Item.builder().name("test1").category(category).build();
        Item item2=Item.builder().name("test2").category(category).build();
        Item item3=Item.builder().name("test3").category(category).build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }


    @Nested
    class TestGetItems {

        @Test
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all items")
        public void getItems() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/items/getAllItems")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            List<ItemDto> actualItems = mapper.readValue(responseString, new TypeReference<>() {
            });


            Assertions.assertEquals(itemRepository.findAll().size(), actualItems.size());

        }

        @Test
        @Transactional
        @WithMockUser(username = "ADMIN")
        @DisplayName("Testing the endpoint for retrieving all items when no items are in database")
        public void getItemsEmpty() throws Exception {

            itemRepository.deleteAll();
            MvcResult result = mockMvc.perform(get("/api/items/getAllItems")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("There are no items registered in the database", responseString);

        }
    }
/* Endpoints returns 404
    @Nested
    class TestSaveItem {
        @Test
        @Transactional
        @DisplayName("Testing the endpoint for saving a Item to database")
        public void saveItem() throws Exception {

            Category category=Category.builder().description("category1").build();

            categoryRepository.save(category);

            ItemDto newItemDto = ItemDto.builder().name("newTest").categoryId(2).build();


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

            Assertions.assertEquals("Item is saved to database", responseString);
            Assertions.assertEquals(newItemDto.getName(), retrievedItem.getName());

        }

        @Test
        @DisplayName("Testing the endpoint for saving a item to database when it already exists")
        public void saveExistingItem() throws Exception {

            ItemDto existingItemDto = ItemDto.builder().name("test").categoryId(1).build();


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
    }*/
}