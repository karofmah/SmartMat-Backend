package idatt2106v231.backend.dto.shoppinglist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyMenuShoppingListDto {

    private int shoppingListId;
    private int subUserId;
    private List<String> ingredients;
}
