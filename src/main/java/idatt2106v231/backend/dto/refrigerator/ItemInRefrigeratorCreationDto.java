package idatt2106v231.backend.dto.refrigerator;

import idatt2106v231.backend.enums.Measurement;
import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRefrigeratorCreationDto {

    private String itemName;
    private int refrigeratorId;
    private double amount;
    private String date;
    private Measurement measurementType;
}
