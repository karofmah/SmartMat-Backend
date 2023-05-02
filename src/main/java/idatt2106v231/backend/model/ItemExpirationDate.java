package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Measurement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

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
    @Temporal(TemporalType.DATE)
    //Prøver java.util, mulig må bruke java.sql
    private Date date;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemRefrigeratorId", referencedColumnName = "itemRefrigeratorId")
    private ItemRefrigerator itemRefrigerator;

    /**
     * Method to update amount
     *
     * @param newAmount the amount to add or remove
     */
    public void addAmount(double newAmount){
        this.amount += newAmount;
    }
}
