package idatt2106v231.backend.dto.refrigerator;

import lombok.*;
import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemExpirationDateDto {

    private int itemExpirationDateId;
    private double amount;
    private Date date;
}