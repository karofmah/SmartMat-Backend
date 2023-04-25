package idatt2106v231.backend.dto.user;

import lombok.Data;

@Data
public class UserAuthenticationDto {
    private String email;
    private String password;
}
