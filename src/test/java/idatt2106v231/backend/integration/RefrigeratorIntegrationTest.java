package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.refrigerator.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ItemExpirationDateRepository itemExpDateRepo;

    @Autowired
    private GarbageRepository garbageRepo;

    private SimpleDateFormat format;


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

        User user2 = User.builder().
                email("test2@ntnu.no")
                .firstName("First name")
                .lastName("Last name")
                .phoneNumber(39183940)
                .age(20)
                .password("123")
                .household(4)
                .build();

        Category category = Category.builder()
                .description("Drinks")
                .build();

        Item item1 = Item.builder()
                .name("milk")
                .category(category)
                .build();

        Item item2 = Item.builder()
                .name("orange juice")
                .category(category)
                .build();

        Item item3 = Item.builder()
                .name("pasta")
                .category(category)
                .build();

        Refrigerator refrigerator = Refrigerator.builder()
                .user(user1)
                .build();

        Refrigerator refrigerator2 = Refrigerator.builder()
                .refrigeratorId(2)
                .user(user2)
                .build();

        ItemRefrigerator itemRefrigerator1 = ItemRefrigerator.builder()
                .item(item1)
                .measurementType(Measurement.L)
                .refrigerator(refrigerator)
                .build();

        ItemRefrigerator itemRefrigerator2_1 = ItemRefrigerator.builder()
                .item(item1)
                .measurementType(Measurement.L)
                .refrigerator(refrigerator2)
                .build();

        ItemRefrigerator itemRefrigerator2_2 = ItemRefrigerator.builder()
                .item(item2)
                .measurementType(Measurement.L)
                .refrigerator(refrigerator2)
                .build();

        ItemRefrigerator itemRefrigerator2_3 = ItemRefrigerator.builder()
                .item(item3)
                .measurementType(Measurement.L)
                .refrigerator(refrigerator2)
                .build();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date1 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 4);
        Date date2 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 10);
        Date date3 = calendar.getTime();

        ItemExpirationDate itemExpirationDate1 = ItemExpirationDate.builder()
                .itemExpirationDateId(1)
                .amount(2.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                .itemRefrigerator(itemRefrigerator1)
                .build();

        ItemExpirationDate itemExpirationDate2 = ItemExpirationDate.builder()
                .itemExpirationDateId(2)
                .amount(2.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                .itemRefrigerator(itemRefrigerator1)
                .build();

        ItemExpirationDate itemExpirationDate2_1 = ItemExpirationDate.builder()
                .itemExpirationDateId(3)
                .amount(2.0)
                .date(format.parse(format.format(date1)))
                .itemRefrigerator(itemRefrigerator2_1)
                .build();

        ItemExpirationDate itemExpirationDate2_2 = ItemExpirationDate.builder()
                .itemExpirationDateId(4)
                .amount(2.0)
                .date(format.parse(format.format(date2)))
                .itemRefrigerator(itemRefrigerator2_2)
                .build();

        ItemExpirationDate itemExpirationDate2_3 = ItemExpirationDate.builder()
                .itemExpirationDateId(5)
                .amount(2.0)
                .date(format.parse(format.format(date3)))
                .itemRefrigerator(itemRefrigerator2_3)
                .build();

        ItemExpirationDate itemExpirationDate2_4 = ItemExpirationDate.builder()
                .itemExpirationDateId(6)
                .amount(2.0)
                .date(format.parse(format.format(date3)))
                .itemRefrigerator(itemRefrigerator2_3)
                .build();

        Garbage garbage = Garbage.builder()
                .refrigerator(refrigerator)
                .amount(1)
                .build();

        userRepo.save(user1);
        userRepo.save(user2);

        catRepo.save(category);

        itemRepo.save(item1);
        itemRepo.save(item2);
        itemRepo.save(item3);

        refRepo.save(refrigerator);
        refRepo.save(refrigerator2);

        itemRefRepo.save(itemRefrigerator1);
        itemRefRepo.save(itemRefrigerator2_1);
        itemRefRepo.save(itemRefrigerator2_2);
        itemRefRepo.save(itemRefrigerator2_3);

        itemExpDateRepo.save(itemExpirationDate1);
        itemExpDateRepo.save(itemExpirationDate2);
        itemExpDateRepo.save(itemExpirationDate2_1);
        itemExpDateRepo.save(itemExpirationDate2_2);
        itemExpDateRepo.save(itemExpirationDate2_3);
        itemExpDateRepo.save(itemExpirationDate2_4);

        garbageRepo.save(garbage);
    }

    @Nested
    class UpdateItemInRefrigerator {

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test updating item date all args ok")
        public void updateItemDateAllArgsOk() throws Exception {
            format = new SimpleDateFormat("yyyy-MM-dd");

            EditItemInRefrigeratorDto updatedInfo = EditItemInRefrigeratorDto.builder()
                    .itemExpirationDateId(1)
                    .date(format.parse("2024-05-01"))
                    .amount(2.0)
                    .measurementType(Measurement.L)
                    .build();

            String content = objectMapper.writeValueAsString(updatedInfo);

            MvcResult result = mockMvc.perform(put("/api/refrigerators/updateItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Optional<ItemExpirationDate> updatedItem = itemExpDateRepo.findById(1);


            Assertions.assertTrue(updatedItem.isPresent());
            Assertions.assertEquals(format.parse("2024-05-01"), updatedItem.get().getDate());
            Assertions.assertEquals("Item is updated", response);
        }

        @Test
        @WithMockUser(username = "USER")
        @DisplayName("Test updating item date when new date is in the past")
        public void updateItemDateNewDateIsInThePast() throws Exception {
            format = new SimpleDateFormat("yyyy-MM-dd");

            EditItemInRefrigeratorDto updatedInfo = EditItemInRefrigeratorDto.builder()
                    .itemExpirationDateId(2)
                    .date(format.parse("2022-05-01"))
                    .amount(2.0)
                    .measurementType(Measurement.L)
                    .build();

            String content = objectMapper.writeValueAsString(updatedInfo);

            MvcResult result = mockMvc.perform(put("/api/refrigerators/updateItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Assertions.assertEquals("New date is in the past", responseString);
        }
    }

    @Nested
    class GetRefrigerator {

        @Test
        @Transactional
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
            Assertions.assertEquals("Refrigerator does not exist", responseString);
        }
    }

    @Nested
    class GetRefrigeratorByExpirationDate {

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Get correct items from refrigerator based on expiration date")
        public void getItemsFromRefrigeratorByExpirationDate() throws Exception {
            format = new SimpleDateFormat("yyyy-MM-dd");

            MvcResult result = mockMvc.perform(get("/api/refrigerators/getItemInRefrigeratorByExpirationDate/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            List<ItemInRefrigeratorDto> retrievedItemsRefrigerator = objectMapper.readValue(responseString, new TypeReference<>() {});


            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date date1 = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 4);
            Date date2 = calendar.getTime();

            Assertions.assertEquals( 2, retrievedItemsRefrigerator.size());
            Assertions.assertEquals(format.format(date1), format.format(retrievedItemsRefrigerator.get(0).getItemsInRefrigerator().get(0).getDate()));
            Assertions.assertEquals(format.format(date2), format.format(retrievedItemsRefrigerator.get(1).getItemsInRefrigerator().get(0).getDate()));
        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Get items by expiration date, refrigerator does not exist")
        public void getItemsByExpirationDateRefrigeratorDoesNotExist() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/refrigerators/getRefrigeratorByUser?userEmail=test30@ntnu.no")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Refrigerator does not exist", responseString);
        }
    }

    @Nested
    class AddItemToRefrigerator {
        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Testing the endpoint for adding item to refrigerator")
        public void addItemToRefrigeratorIsCreated() throws Exception {
            ItemInRefrigeratorCreationDto newItem = ItemInRefrigeratorCreationDto.builder()
                    .itemName("orange juice")
                    .refrigeratorId(1)
                    .amount(1)
                    .measurementType(Measurement.L)
                    .build();

            String contentJson = objectMapper.writeValueAsString(newItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(contentJson))
                    .andExpect(status().isCreated())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();

            Optional<ItemRefrigerator> itemOptional = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId("orange juice",1);

            Assertions.assertTrue(itemOptional.isPresent());
            ItemRefrigerator retrievedItem = itemOptional.get();

            Assertions.assertEquals("New item is added to database", responseString);
            Assertions.assertEquals(newItem.getItemName(), retrievedItem.getItem().getName());
        }

        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Testing the endpoint for adding item to refrigerator when item already exists in that refrigerator")
        public void addItemToRefrigeratorIsOk() throws Exception {

            ItemInRefrigeratorCreationDto existingItem = ItemInRefrigeratorCreationDto.builder()
                    .itemName("pasta")
                    .refrigeratorId(2)
                    .amount(4)
                    .measurementType(Measurement.DL)
                    .build();

            int sizeBefore = itemRefRepo.findById(3).get().getItemExpirationDates().size();

            String contentJson = objectMapper.writeValueAsString(existingItem);

            MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(contentJson))
                    .andExpect(status().isCreated())
                    .andReturn();


            String responseString = result.getResponse().getContentAsString();
            int sizeAfter = itemRefRepo.findById(1).get().getItemExpirationDates().size();

            Assertions.assertEquals(sizeBefore + 1, sizeAfter);
            Assertions.assertEquals("New item is added to database", responseString);
        }

        @Nested
        class AddItemToRefrigeratorIsNotFound{

            @Test
            @Transactional
            @WithMockUser("USER")
            @DisplayName("Testing the endpoint for adding an item to refrigerator when item does not exist")
            public void addItemToRefrigeratorItemIsNotFound() throws Exception {

                ItemInRefrigeratorCreationDto notExistingItem = ItemInRefrigeratorCreationDto.builder()
                        .itemName("cheese")
                        .refrigeratorId(1)
                        .amount(1)
                        .measurementType(Measurement.KG)
                        .build();

                String contentJson = objectMapper.writeValueAsString(notExistingItem);

                MvcResult result = mockMvc.perform(post("/api/refrigerators/addItemInRefrigerator")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contentJson))
                        .andExpect(status().isNotFound())
                        .andReturn();

                String responseString = result.getResponse().getContentAsString();
                Assertions.assertEquals("Item does not exist", responseString);
            }

            @Test
            @Transactional
            @WithMockUser("USER")
            @DisplayName("Testing the endpoint for adding an item to refrigerator when refrigerator does not exist ")
            public void addItemToRefrigeratorRefrigeratorIsNotFound() throws Exception {
                ItemInRefrigeratorCreationDto existingItem = ItemInRefrigeratorCreationDto.builder()
                        .itemName("test10")
                        .refrigeratorId(30)
                        .amount(1)
                        .measurementType(Measurement.KG)
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
        @WithMockUser("USER")
        @DisplayName("Testing the endpoint for saving an item to refrigerator when item is not valid")
        public void addItemToRefrigeratorIsBadRequest() throws Exception {
            ItemInRefrigeratorCreationDto existingItem = ItemInRefrigeratorCreationDto.builder()
                    .itemName("")
                    .refrigeratorId(30)
                    .amount(1)
                    .measurementType(Measurement.UNIT)
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
            ItemInRefrigeratorRemovalDto itemToRemove = ItemInRefrigeratorRemovalDto.builder()
                    .itemExpirationDateId(1)
                    .amount(2.0)
                    .garbage(false)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            int size = itemExpDateRepo.findAll().size();

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(size-1, itemExpDateRepo.findAll().size());
            Assertions.assertEquals("Item is removed from refrigerator", responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item from refrigerator and throw total amount in garbage")
        public void removeItemFromRefrigeratorAndThrowInGarbageIsOk() throws Exception {
            ItemInRefrigeratorRemovalDto itemToRemove = ItemInRefrigeratorRemovalDto.builder()
                    .itemExpirationDateId(2)
                    .amount(2)
                    .garbage(true)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            int itemInRefrigeratorSize = itemExpDateRepo.findAll().size();
            int garbageSize = garbageRepo.findAll().size();

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals(itemInRefrigeratorSize-1, itemExpDateRepo.findAll().size());
            Assertions.assertEquals(garbageSize + 1, garbageRepo.findAll().size());
            Assertions.assertEquals(2, garbageRepo.findByRefrigeratorRefrigeratorIdAndDate(1, YearMonth.now()).get().getAmount());
            Assertions.assertEquals("Item is removed from refrigerator and thrown in garbage", responseString);
        }
        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of an amount of an item that is less than total from refrigerator")
        public void removeAmountOfItemFromRefrigeratorIsOk() throws Exception {
            ItemInRefrigeratorRemovalDto itemToRemove = ItemInRefrigeratorRemovalDto.builder()
                    .itemExpirationDateId(3)
                    .amount(0.5)
                    .garbage(false)
                    .build();

            String itemToRemoveJson = objectMapper.writeValueAsString(itemToRemove);

            MvcResult result = mockMvc.perform((MockMvcRequestBuilders.delete("/api/refrigerators/removeItem")
                            .accept(MediaType.APPLICATION_JSON))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemToRemoveJson))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            Optional<ItemExpirationDate> itemExpirationDate = itemExpDateRepo.findById(3);


            Assertions.assertEquals(1.5 , itemExpDateRepo.findById(3).get().getAmount());
            Assertions.assertTrue(itemExpirationDate.isPresent());
            Assertions.assertEquals("Item is removed from refrigerator",responseString);
        }

        @Test
        @WithMockUser(username = "USER")
        @Transactional
        @DisplayName("Test removal of item when item is not found in refrigerator")
        public void removeItemFromRefrigeratorIsNotFound() throws Exception {
            int size = itemRefRepo.findAll().size();

            ItemInRefrigeratorCreationDto itemToRemove = ItemInRefrigeratorCreationDto.builder()
                    .itemName("orange juice")
                    .refrigeratorId(1)
                    .amount(1)
                    .measurementType(Measurement.L)
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