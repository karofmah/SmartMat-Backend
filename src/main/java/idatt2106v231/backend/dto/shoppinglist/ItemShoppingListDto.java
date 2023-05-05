package idatt2106v231.backend.dto.shoppinglist;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.enums.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemShoppingListDto {

    private ItemDto item;
    private int amount;
    private Measurement measurementType;
    private boolean subUserAccessLevel;
    private int itemShoppingListId;
    private int subUserId;
}
