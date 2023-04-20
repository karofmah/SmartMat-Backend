package idatt2106v231.backend.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {

    private String name;
    private int category;

    public ItemDto(){
        category = -1; //default value
    }
}
