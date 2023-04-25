package idatt2106v231.backend.dto.refrigerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefrigeratorDto {

    private String userEmail;
    private List<ItemInRefrigeratorDto> items;
}