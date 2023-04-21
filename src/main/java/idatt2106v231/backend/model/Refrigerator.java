package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Refrigerator {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refrigeratorId;

    @Column
    @NotNull
    private String user;

    @OneToMany(mappedBy = "refrigerator")
    private List<ItemRefrigerator> itemsInRefrigerator = new ArrayList<>();
}
