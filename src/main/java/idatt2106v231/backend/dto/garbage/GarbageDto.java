package idatt2106v231.backend.dto.garbage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GarbageDto {

    private int refrigeratorId;

    private int year;
}
