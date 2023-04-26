package idatt2106v231.backend.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
public class ItemDto {

    private String name;
    private int categoryId;

    public ItemDto(){
        categoryId = -1; //default value
    }
}