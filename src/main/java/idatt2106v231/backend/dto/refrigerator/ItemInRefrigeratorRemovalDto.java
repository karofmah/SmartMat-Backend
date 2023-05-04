package idatt2106v231.backend.dto.refrigerator;


import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRefrigeratorRemovalDto {

    private int itemExpirationDateId;
    private double amount;
    private boolean garbage;
}
