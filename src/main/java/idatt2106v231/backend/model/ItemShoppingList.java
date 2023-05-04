package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Measurement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ItemShoppingList {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemShoppingListId;

    @Column
    @NotNull
    private double amount;

    @Column
    @NotNull
    private Measurement measurementType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shoppingListId", referencedColumnName = "shoppingListId")
    private ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemId", referencedColumnName = "itemId")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subUserId", referencedColumnName = "subUserId")
    private SubUser subUser;
}
