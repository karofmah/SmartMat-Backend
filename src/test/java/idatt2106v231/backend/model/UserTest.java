package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User testUser1 = new User("testmail@ntnu.no", "password", "firstname", "lastname", 10101010, 10, 4, Role.USER, null, null);
    User testUser2 = new User();

    @Test
    void isAccountNonExpired() {
        assertTrue(testUser1.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        assertTrue(testUser1.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(testUser1.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        assertTrue(testUser1.isEnabled());
    }

    @Test
    void getAge() {
        assertEquals(10, testUser1.getAge());
    }

    @Test
    void setFirstName() {
        testUser1.setFirstName("firstName2");
        assertEquals("firstName2", testUser1.getFirstName());
    }

    @Test
    void setLastName() {
        testUser1.setLastName("lastName2");
        assertEquals("lastName2", testUser1.getLastName());
    }

    @Test
    void setHouseHold() {
        testUser1.setHousehold(5);
        assertEquals(5, testUser1.getHousehold());
    }

    @Test
    void setPhoneNumber() {
        testUser1.setPhoneNumber(50505050);
        assertEquals(50505050, testUser1.getPhoneNumber());
    }

    @Test
    void setPassword() {
        testUser1.setPassword("newPassword");
        assertEquals("newPassword", testUser1.getPassword());
    }

    @Test
    void setRole() {
        testUser1.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, testUser1.getRole());
    }

    @Test
    void testEquals() {
        assertNotEquals(testUser1, testUser2);
    }
}