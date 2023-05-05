package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    @Column(unique = true)
    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    @NotNull
    private Category category;

    @OneToMany(mappedBy = "item",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRefrigerator> itemInRefrigerators = new ArrayList<>();

    @OneToMany(mappedBy = "item",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemShoppingList> itemInShoppingList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name) || itemId==item.getItemId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}