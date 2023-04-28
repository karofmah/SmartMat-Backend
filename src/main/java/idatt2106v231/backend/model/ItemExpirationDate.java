package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Measurement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class ItemExpirationDate {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemExpirationDateId;

    @Column
    @NotNull
    private Measurement measurement;

    @Column
    @NotNull
    private double amount;

    @Column
    private java.sql.Date date;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemRefrigeratorId", referencedColumnName = "itemRefrigeratorId")
    private ItemRefrigerator itemRefrigerator;
}
