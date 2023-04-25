package idatt2106v231.backend.dto.shoppinglist;

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
    //private String user;
    private String userEmail;
    private String itemName;
    private int amount;
    private Measurement measurement;
}
