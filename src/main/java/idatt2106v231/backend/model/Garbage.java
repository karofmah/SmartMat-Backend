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
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Garbage {
    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int garbageId;

    @Column
    @NotNull
    private int amount;

    @OneToMany(mappedBy = "garbage")
    private List<GarbageRefrigerator> garbageRefrigerators = new ArrayList<>(); //??


}
