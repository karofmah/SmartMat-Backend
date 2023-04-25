package idatt2106v231.backend.dto.refrigerator;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefrigeratorDto {

    private int refrigeratorId;
    private List<ItemInRefrigeratorDto> items;
}