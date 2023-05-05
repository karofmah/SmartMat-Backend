package idatt2106v231.backend.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


class RefrigeratorTest {

    @Test
    void getRefrigeratorId() {
        Refrigerator ref = Refrigerator.builder()
                .user(new User())
                .refrigeratorId(1)
                .build();

        Assertions.assertEquals(ref.getRefrigeratorId(), 1);
    }

    @Test
    void getUser() {
        User user = User.builder()
                .firstName("ole")
                .email("ole@mail.no")
                .build();


        Refrigerator ref = Refrigerator.builder()
                .user(user)
                .refrigeratorId(1)
                .build();

        Assertions.assertEquals(ref.getUser(), user);
        Assertions.assertEquals(ref.getUser().getFirstName(), user.getFirstName());
    }

    @Test
    void setUser() {
        User user = User.builder()
                .firstName("ole")
                .email("ole@mail.no")
                .build();

        Refrigerator ref = Refrigerator.builder()
                .user(user)
                .refrigeratorId(1)
                .build();

        Assertions.assertEquals(ref.getUser(), user);

        ref.setUser(new User());
        Assertions.assertNotEquals(ref.getUser(), user);
    }

    @Test
    void testEquals() {

        User user = User.builder()
                .firstName("ole")
                .email("ole@mail.no")
                .build();

        User user2 = User.builder()
                .firstName("einar")
                .email("einar@mail.no")
                .build();

        Refrigerator ref = Refrigerator.builder()
                .user(user)
                .refrigeratorId(1)
                .build();

        Refrigerator ref2 = Refrigerator.builder()
                .user(user)
                .refrigeratorId(1)
                .build();

        Refrigerator ref3 = Refrigerator.builder()
                .user(user2)
                .refrigeratorId(2)
                .build();

        Assertions.assertEquals(ref, ref2);
        Assertions.assertNotEquals(ref, ref3);
    }
}