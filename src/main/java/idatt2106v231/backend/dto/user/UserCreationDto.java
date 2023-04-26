package idatt2106v231.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int age;
    private int household;
}