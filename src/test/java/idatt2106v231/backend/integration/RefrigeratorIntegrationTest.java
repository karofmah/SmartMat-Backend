package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.*;
import idatt2106v231.backend.repository.*;
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

import java.time.YearMonth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test4",
        "spring.datasource.url=jdbc:h2:mem:test4;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class RefrigeratorIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRefrigeratorRepository itemRefRepo;

    @Autowired
    private RefrigeratorRepository refRepo;

    @Autowired
    private ItemExpirationDateRepository itemExpirationDateRepository;

    @Autowired
    private GarbageRepository garbageRepo;


    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup() throws ParseException {

        User user1 = User.builder().
                email("test1@ntnu.no")
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber(39183940)
                .age(20)
                .password("123")
                .household(4)
                .build();

        Category category = Category.builder()
                .description("category1")
                .build();

        Item item1 = Item.builder()
                .name("test10")
                .category(category)
                .build();

        Item item2 = Item.builder()
                .name("test11")
                .category(category)
                .build();


        Refrigerator refrigerator = Refrigerator.builder()
                .user(user1)
                .build();

        ItemRefrigerator itemRefrigerator1 = ItemRefrigerator.builder()
                .item(item1)
                .refrigerator(refrigerator)
                .build();

        Garbage garbage = Garbage.builder()
                .refrigerator(refrigerator)
                .amount(0)
                .date(YearMonth.now())
                .build();

        userRepo.save(user1);
        catRepo.save(category);
        itemRepo.save(item1);
        itemRepo.save(item2);
        refRepo.save(refrigerator);
        itemRefRepo.save(itemRefrigerator1);
        garbageRepo.save(garbage);

        ItemExpirationDate itemExpirationDate1 = ItemExpirationDate.builder()
                .amount(2.0)
                .measurement(Measurement.L)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                .itemRefrigerator(itemRefrigerator1)
                .build();

        itemExpirationDateRepository.save(itemExpirationDate1);

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


            EditItemInRefrigeratorDto newItem= EditItemInRefrigeratorDto.builder()
                    .itemName("test11")
                    .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                    .refrigeratorId(1)
                    .amount(1)
                    .measurementType(Measurement.KG)
                    .build();

            String newRefrigeratorJson = objectMapper.writeValueAsString(newItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newRefrigeratorJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<ItemRefrigerator> itemOptional = itemRefRepo
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

            EditItemInRefrigeratorDto existingItem= EditItemInRefrigeratorDto.builder()
                    .itemName("test10")
                    .refrigeratorId(1)
                    .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                    .measurementType(Measurement.KG)
                    .amount(1.5)
                    .build();

            String existingRefrigeratorJson = objectMapper.writeValueAsString(existingItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(existingRefrigeratorJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Optional<ItemRefrigerator> itemInRefrigerator=itemRefRepo
                    .findById(1);

            Optional<ItemExpirationDate> itemExpirationDate = itemExpirationDateRepository
                    .findById(1);

            Assertions.assertTrue(itemExpirationDate.isPresent());
            Assertions.assertTrue(itemInRefrigerator.isPresent());
            Assertions.assertEquals("Item is updated", responseString);
            Assertions.assertEquals(3.5, itemExpirationDate.get().getAmount());
        }

        @Nested
        class AddItemToRefrigeratorIsNotFound{

            @Test
            @DisplayName("Testing the endpoint for adding an item to refrigerator when item does not exist")
            public void addItemToRefrigeratorItemIsNotFound() throws Exception {

                EditItemInRefrigeratorDto existingItem = EditItemInRefrigeratorDto.builder()
                        .itemName("test30")
                        .refrigeratorId(1)
                        .amount(1)
                        .build();

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
                EditItemInRefrigeratorDto existingItem = EditItemInRefrigeratorDto.builder()
                        .itemName("test10")
                        .refrigeratorId(30)
                        .amount(1)
                        .build();

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
            EditItemInRefrigeratorDto existingItem = EditItemInRefrigeratorDto.builder()
                    .itemName("")
                    .refrigeratorId(30)
                    .amount(1)
                    .build();

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
    class RemoveItemFromRefrigerator{

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item from refrigerator")
        public void removeItemFromRefrigeratorIsOk() throws Exception {
            EditItemInRefrigeratorDto itemToRemove = EditItemInRefrigeratorDto.builder()
                    .itemName("test10")
                    .refrigeratorId(1)
                    .amount(2)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);
            int size = itemRefRepo.findAll().size();

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem?isGarbage=false")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size-1, itemRefRepo.findAll().size());
            Assertions.assertEquals("Item is removed from refrigerator",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item from refrigerator and throw total amount in garbage")
        public void removeItemFromRefrigeratorAndThrowInGarbageIsOk() throws Exception {

            int itemInRefrigeratorSize = itemRefRepo.findAll().size();
            int garbageSize = garbageRepo.findAll().size();

            EditItemInRefrigeratorDto itemToRemove = EditItemInRefrigeratorDto
                    .builder()
                    .itemName("test10")
                    .refrigeratorId(1)
                    .amount(2)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem?isGarbage=true")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(itemInRefrigeratorSize-1, itemRefRepo.findAll().size());
            Assertions.assertEquals(garbageSize, garbageRepo.findAll().size());
            Assertions.assertEquals(2, garbageRepo.findByRefrigeratorRefrigeratorIdAndDate(1, YearMonth.now()).get().getAmount());
            Assertions.assertEquals("Item is removed from refrigerator and thrown in garbage",responseString);
        }
        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of an amount of an item that is less than total from refrigerator")
        public void removeAmountOfItemFromRefrigeratorIsOk() throws Exception {

            double totalAmount=itemExpirationDateRepository.findById(1).get().getAmount();

            EditItemInRefrigeratorDto itemToRemove= EditItemInRefrigeratorDto.builder()
                    .itemName("test10")
                    .refrigeratorId(1)
                    .amount(0.5)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem?isGarbage=false")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(1.5 , itemExpirationDateRepository.findById(1).get().getAmount());
            Assertions.assertEquals("Item is removed from refrigerator",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item when refrigerator is not found in database")
        public void removeItemFromRefrigeratorIsNotFound() throws Exception {

            int size = itemRefRepo.findAll().size();

            EditItemInRefrigeratorDto itemToRemove = EditItemInRefrigeratorDto.builder()
                    .itemName("test11")
                    .refrigeratorId(1)
                    .amount(1)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result=mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem?isGarbage=false")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size, itemRefRepo.findAll().size());
            Assertions.assertEquals("Item does not exist in refrigerator",responseString);
        }
    }
}