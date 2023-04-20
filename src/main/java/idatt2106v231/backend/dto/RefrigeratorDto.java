package idatt2106v231.backend.dto;

import idatt2106v231.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefrigeratorDto {

    private User user;
}
