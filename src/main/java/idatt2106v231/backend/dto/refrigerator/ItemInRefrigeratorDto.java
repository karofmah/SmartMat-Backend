package idatt2106v231.backend.dto.refrigerator;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.enums.Measurement;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRefrigeratorDto {

    private int itemRefrigeratorId;
    private ItemDto item;
    private Measurement measurementType;
    private List<ItemExpirationDateDto> itemsInRefrigerator;
}