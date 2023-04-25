package idatt2106v231.backend.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {

    private String email;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int household;
}
