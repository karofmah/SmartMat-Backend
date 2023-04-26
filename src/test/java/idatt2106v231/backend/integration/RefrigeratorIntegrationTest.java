package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorCreationDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.model.*;
import idatt2106v231.backend.repository.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test4",
        "spring.datasource.url=jdbc:h2:mem:test4",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class RefrigeratorIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRefrigeratorRepository itemRefrigeratorRepository;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    private static JdbcDataSource dataSource;


    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup() {

        User user1=User.builder().
                email("test1@ntnu.no")
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber(39183940)
                .age(20)
                .password("123")
                .household(4)
                .build();

        userRepository.save(user1);

        Category category = Category.builder()
                .description("category1")
                .build();

        categoryRepository.save(category);

        Item item1 = Item.builder()
                .name("test10")
                .category(category)
                .build();

        Item item2 = Item.builder()
                .name("test11")
                .category(category)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Refrigerator refrigerator=Refrigerator.builder()
                .user(user1)
                .build();

        refrigeratorRepository.save(refrigerator);

        ItemRefrigerator itemRefrigerator1=ItemRefrigerator.builder()
                .item(item1)
                .refrigerator(refrigerator)
                .amount(2)
                .build();


        itemRefrigeratorRepository.save(itemRefrigerator1);


    }


    @Nested
    class GetRefrigerator {

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting an Refrigerator that exists in database")
        public void getItemsInRefrigeratorIsOk() throws Exception {


            MvcResult result = mockMvc.perform(get("/api/refrigerators/getRefrigeratorByUser?userEmail=test1@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            RefrigeratorDto retrievedItemsRefrigerator = objectMapper.readValue(responseString, new TypeReference<>() {
            });
            Assertions.assertEquals( 1,retrievedItemsRefrigerator.getItems().size());


        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test getting an refrigerators that does not exist in database")
        public void getItemsInRefrigeratorIsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/refrigerators/getRefrigeratorByUser?userEmail=test30@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            System.out.println("Response: " + responseString);
            Assertions.assertEquals("Refrigerator does not exist", responseString);


        }

    }


    @Nested
    class AddItemToRefrigerator {
        @Test
        @Transactional
        @DisplayName("Testing the endpoint for adding item to refrigerator")
        public void addItemToRefrigeratorIsCreated() throws Exception {


            ItemInRefrigeratorCreationDto newItem= ItemInRefrigeratorCreationDto.builder().itemName("test11").refrigeratorId(1).amount(1).build();

            String newRefrigeratorJson = objectMapper.writeValueAsString(newItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newRefrigeratorJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<ItemRefrigerator> itemOptional = itemRefrigeratorRepository
                    .findByItemNameAndRefrigeratorRefrigeratorId("test11",1);
            Assertions.assertTrue(itemOptional.isPresent());
            ItemRefrigerator retrievedItem = itemOptional.get();

            Assertions.assertEquals("Item is added to refrigerator", responseString);
            Assertions.assertEquals(newItem.getItemName(), retrievedItem.getItem().getName());

        }

        @Test
        @Transactional
        @DisplayName("Testing the endpoint for adding item to refrigerator when item already exists in that refrigerator")
        public void addItemToRefrigeratorIsOk() throws Exception {

            ItemInRefrigeratorCreationDto existingItem= ItemInRefrigeratorCreationDto.builder().itemName("test10").refrigeratorId(1).amount(1).build();

            String existingRefrigeratorJson = objectMapper.writeValueAsString(existingItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingRefrigeratorJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<ItemRefrigerator> itemInRefrigerator=itemRefrigeratorRepository
                    .findByItemNameAndRefrigeratorRefrigeratorId("test10",1);
            Assertions.assertTrue(itemInRefrigerator.isPresent());
            Assertions.assertEquals("Item is updated", responseString);
            Assertions.assertEquals(3,itemInRefrigerator.get().getAmount());
        }

        @Nested
        class AddItemToRefrigeratorIsNotFound{

            @Test
            @DisplayName("Testing the endpoint for adding an item to refrigerator when item does not exist")
            public void addItemToRefrigeratorItemIsNotFound() throws Exception {

                ItemInRefrigeratorCreationDto existingItem= ItemInRefrigeratorCreationDto.builder().itemName("test30").refrigeratorId(1).amount(1).build();

                String existingRefrigeratorJson = objectMapper.writeValueAsString(existingItem);

                MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(existingRefrigeratorJson))
                        .andExpect(status().isNotFound())
                        .andReturn();

                String responseString = result.getResponse().getContentAsString();

                Assertions.assertEquals("Item does not exist", responseString);

            }
            @Test
            @DisplayName("Testing the endpoint for adding an item to refrigerator when refrigerator does not exist ")
            public void addItemToRefrigeratorRefrigeratorIsNotFound() throws Exception {

                ItemInRefrigeratorCreationDto existingItem= ItemInRefrigeratorCreationDto.builder().itemName("test10").refrigeratorId(30).amount(1).build();

                String existingRefrigeratorJson = objectMapper.writeValueAsString(existingItem);

                MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(existingRefrigeratorJson))
                        .andExpect(status().isNotFound())
                        .andReturn();

                String responseString = result.getResponse().getContentAsString();

                Assertions.assertEquals("Refrigerator does not exist", responseString);

            }
        }
        @Test
        @DisplayName("Testing the endpoint for saving an item to refrigerator when item is not valid")
        public void addItemToRefrigeratorIsBadRequest() throws Exception {

            ItemInRefrigeratorCreationDto existingItem= ItemInRefrigeratorCreationDto.builder().itemName("").refrigeratorId(30).amount(1).build();


            String existingRefrigeratorJson = objectMapper.writeValueAsString(existingItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingRefrigeratorJson))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Data is not valid", responseString);

        }
    }
    @Nested
    class DeleteRefrigerator{

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item from refrigerator")
        public void removeItemFromRefrigeratorIsOk() throws Exception {

            int size = itemRefrigeratorRepository.findAll().size();

            ItemInRefrigeratorCreationDto itemToRemove= ItemInRefrigeratorCreationDto.builder().itemName("test10").refrigeratorId(1).amount(2).build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItemFromRefrigerator")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size-1,itemRefrigeratorRepository.findAll().size());
            Assertions.assertEquals("Item is removed from refrigerator",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test deletion of refrigerator when refrigerator is not found in database")
        public void removeItemFromRefrigeratorIsNotFound() throws Exception {

            int size = itemRefrigeratorRepository.findAll().size();

            ItemInRefrigeratorCreationDto itemToRemove= ItemInRefrigeratorCreationDto.builder().itemName("test11").refrigeratorId(1).amount(1).build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItemFromRefrigerator")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size,itemRefrigeratorRepository.findAll().size());
            Assertions.assertEquals("Item does not exist in refrigerator",responseString);

        }
    }
}
