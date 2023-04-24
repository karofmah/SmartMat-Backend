package idatt2106v231.backend.model;

import idatt2106v231.backend.enums.Measurement;
import jakarta.persistence.*;
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
    private int test;

    @Column
    private int amount;

    @Column
    private Measurement measurement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shoppingListId", referencedColumnName = "shoppingListId")
    private ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemId", referencedColumnName = "itemId")
    private Item item;
}
