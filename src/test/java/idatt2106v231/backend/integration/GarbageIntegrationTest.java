package idatt2106v231.backend.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.item.ItemDto;
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

import java.util.List;

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
    private ItemRefrigeratorRepository itemRefrigeratorRepository;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private GarbageRepository garbageRepository;


    @BeforeAll
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



        Garbage garbage1=Garbage.builder().refrigerator(refrigerator).amount(1).build();
        Garbage garbage2=Garbage.builder().refrigerator(refrigerator).amount(2).build();
        Garbage garbage3=Garbage.builder().refrigerator(refrigerator).amount(3).build();

        garbageRepository.save(garbage1);
        garbageRepository.save(garbage2);
        garbageRepository.save(garbage3);

    }
    @Nested
    class averageGarbageAmount{
        @Test
        @WithMockUser("USER")
        @DisplayName("Test calculation of average amount of garbage among all garbages")
        public void averageGarbageAmountIsOk() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/garbages/averageAmount")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            int averageAmount = mapper.readValue(responseString, new TypeReference<>() {
            });


            Assertions.assertEquals(2, averageAmount);

        }
        @Test
        @Transactional
        @WithMockUser("USER")
        @DisplayName("Test calculation of average amount of garbage among all garbages")
        public void averageGarbageAmountIsNotFound() throws Exception {
            garbageRepository.deleteAll();
            MvcResult result = mockMvc.perform(get("/api/garbages/averageAmount")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            String responseString = result.getResponse().getContentAsString();


            Assertions.assertEquals("There are no garbages registered in database", responseString);

        }

    }
}
