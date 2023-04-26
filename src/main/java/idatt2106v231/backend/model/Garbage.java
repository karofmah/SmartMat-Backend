package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
public class Garbage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int garbageRefrigeratorId;

    @Column
    @NotNull
    int year;

    @Column
    @NotNull
    int amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refrigerator_id", referencedColumnName = "refrigeratorId")
    private Refrigerator refrigerator;

    public Garbage() {
        this.year = Year.now().getValue();
    }
}