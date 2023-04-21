package idatt2106v231.backend.dto;

import idatt2106v231.backend.enums.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRefrigeratorDto {

    private String user;
    private int refrigeratorId;
    private int amount;
    private Measurement measurementType;
}
