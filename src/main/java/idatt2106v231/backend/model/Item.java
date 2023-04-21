package idatt2106v231.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    @Column
    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    @NotNull
    private Category category;

    @OneToMany(mappedBy = "item")
    private List<ItemRefrigerator> itemInRefrigerators = new ArrayList<>();

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