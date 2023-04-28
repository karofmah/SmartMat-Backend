package idatt2106v231.backend.dto.subuser;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubUserDto {
    private String name;
    private Boolean accessLevel;
    private String masterUserEmail;
    private int pinCode;
}
