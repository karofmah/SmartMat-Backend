package idatt2106v231.backend.dto.refrigerator;

import idatt2106v231.backend.enums.Measurement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRefrigeratorDto {

    private int refrigeratorId;
    private String itemName;
    private int amount;
    private Measurement measurementType;
}