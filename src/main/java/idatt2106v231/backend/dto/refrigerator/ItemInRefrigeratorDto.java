package idatt2106v231.backend.dto.refrigerator;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.enums.Measurement;
import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRefrigeratorDto {

    private ItemDto item;
    private double amount;
    private Measurement measurementType;
}