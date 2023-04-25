package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class GarbageRefrigerator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int garbageRefrigeratorId;

    @Column
    @NotNull
    int year;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garbage_id", referencedColumnName = "garbageId")
    private Garbage garbage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refrigerator_id", referencedColumnName = "refrigeratorId")
    private Refrigerator refrigerator;
}
