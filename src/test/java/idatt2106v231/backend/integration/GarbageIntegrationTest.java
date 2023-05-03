package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class
        ,properties = {
        "spring.config.name=test8",
        "spring.datasource.url=jdbc:h2:mem:test8;NON_KEYWORDS=YEAR",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class GarbageIntegrationTest {


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
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private GarbageRepository garbageRepository;


    @BeforeAll
    @DisplayName("Add test data to test database")
    public void setup(){

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



        Garbage garbage1=Garbage.builder().refrigerator(refrigerator).amount(1).date(YearMonth.of(2023,3)).build();
        Garbage garbage2=Garbage.builder().refrigerator(refrigerator).amount(2).date(YearMonth.of(2023,2)).build();
        Garbage garbage3=Garbage.builder().refrigerator(refrigerator).amount(3).date(YearMonth.of(2023,4)).build();
        Garbage garbage4=Garbage.builder().refrigerator(refrigerator).amount(3).date(YearMonth.of(2023,4)).build();



        garbageRepository.save(garbage1);
        garbageRepository.save(garbage2);
        garbageRepository.save(garbage3);
        garbageRepository.save(garbage4);


    }
    @Nested
    class TotalGarbageAmountYear{
        @Test
        @WithMockUser("USER")
        @DisplayName("Test calculation of total amount of garbage")
        public void totalGarbageAmountYearIsOk() throws Exception {


            MvcResult result = mockMvc.perform(get("/api/garbages/refrigerator/totalAmountYear/1?year=2023")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            ObjectMapper mapper = new ObjectMapper();
            int totalAmount = mapper.readValue(responseString, new TypeReference<>() {
            });


            Assertions.assertEquals(9, totalAmount);

        }
        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test calculation of total amount of garbage when no refrigerator exist")
        public void totalGarbageAmountYearRefrigeratorIsNotFound() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/garbages/refrigerator/totalAmountYear/30?year=2023")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Refrigerator does not exist", responseString);

        }
        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test calculation of total amount of garbage when refrigerator does not have any garbages")
        public void totalGarbageAmountYearGarbagesIsNotFound() throws Exception {

            garbageRepository.deleteAll();


            MvcResult result = mockMvc.perform(get("/api/garbages/refrigerator/totalAmountYear/1?year=2023")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Refrigerator does not have garbages", responseString);

        }

        @Test
        @WithMockUser("USER")
        @DisplayName("Test calculation of total amount of garbage when year is not specified")
        public void totalGarbageAmountYearIsBadRequest() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/garbages/refrigerator/totalAmountYear/1?year=-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            Assertions.assertEquals("Data is not specified", responseString);

        }
    }


        @Test
        @WithMockUser("USER")
        @DisplayName("Test calculation of amount of garbage each month in a specific year")
        public void amountEachMonthIsOk() throws Exception {

            MvcResult result = mockMvc.perform(get("/api/garbages/refrigerator/amountEachMonth/1?year=2023")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();

            ObjectMapper mapper = new ObjectMapper();
            int[] amountEachMonth = mapper.readValue(responseString, new TypeReference<>() {
            });

            int[] expectedAmountEachMonth=new int[12];
            expectedAmountEachMonth[1]=2;
            expectedAmountEachMonth[2]=1;
            expectedAmountEachMonth[3]=6;

            for (int i = 0; i < amountEachMonth.length; i++) {
                Assertions.assertEquals(expectedAmountEachMonth[i], amountEachMonth[i]);
            }
        }

    }
