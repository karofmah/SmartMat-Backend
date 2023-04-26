package idatt2106v231.backend.dto.shoppinglist;

import idatt2106v231.backend.enums.Measurement;
import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInShoppingListCreationDto {

    private String itemName;
    private int shoppingListId;
    private int amount;
    private Measurement measurementType;
}