package idatt2106v231.backend.service;

import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.model.WeeklyMenu;
import idatt2106v231.backend.repository.UserRepository;
import idatt2106v231.backend.repository.WeekMenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecipeServicesTest {

    @InjectMocks
    private RecipeServices recipeServices;

    @Mock
    private WeekMenuRepository weekMenuRepo;

    @Mock
    private UserRepository userRepo;

    @BeforeEach
    public void setUp() {
        User user1 = User.builder()
                .email("test1@ntnu.no")
                .password("password")
                .firstName("firstName1")
                .lastName("lastName1")
                .phoneNumber(1234)
                .age(11)
                .household(1)
                .role(Role.USER)
                .build();

        WeeklyMenu testWeeklyMenu1 = WeeklyMenu
                .builder()
                .user(user1)
                .menu("TestMenu")
                .build();

        Mockito.lenient().when(weekMenuRepo.findByUserEmail("test1@ntnu.no")).thenReturn(Optional.ofNullable(testWeeklyMenu1));

        Mockito.lenient().when(userRepo.findByEmail("test1@ntnu.no")).thenReturn(Optional.ofNullable(user1));
    }

    @Test
    @DisplayName("Test retrieval of weekly menu")
    public void getWeeklyMenuSuccess() {

        assertEquals("TestMenu", recipeServices.getWeeklyMenu("test1@ntnu.no"));
    }

    @Test
    @DisplayName("Test saving of weekly menu")
    public void saveWeeklyMenuSuccess() {

        assertTrue(recipeServices.saveWeeklyMenu("test1@ntnu.no", "New weekly menu"));
    }
}
