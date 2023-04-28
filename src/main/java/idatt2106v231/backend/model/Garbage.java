package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Year;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
public class Garbage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer garbageId;

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