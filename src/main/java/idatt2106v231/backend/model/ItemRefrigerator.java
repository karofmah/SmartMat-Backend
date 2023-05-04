package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Measurement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class ItemRefrigerator {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemRefrigeratorId;

    @Column
    @NotNull
    private Measurement measurementType;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "itemId")
    private Item item;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refrigerator_id", referencedColumnName = "refrigeratorId")
    private Refrigerator refrigerator;

    @OneToMany(mappedBy = "itemRefrigerator", cascade = CascadeType.REMOVE)
    private List<ItemExpirationDate> itemExpirationDates;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRefrigerator that = (ItemRefrigerator) o;
        return item.equals(that.item) && refrigerator.equals(that.refrigerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, refrigerator);
    }
}