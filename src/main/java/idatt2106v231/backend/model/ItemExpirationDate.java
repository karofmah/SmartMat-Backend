package idatt2106v231.backend.model;

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
    private double amount;

    @Column
    @Temporal(TemporalType.DATE)
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
    public void updateAmount(double newAmount){
        this.amount += newAmount;
    }
}
