package idatt2106v231.backend.dto.subuser;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubUserCreationDto {

    private String name;
    private boolean accessLevel;
    private String userEmail;
    private int pinCode;
}