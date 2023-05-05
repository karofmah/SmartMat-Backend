package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemShoppingList;
import idatt2106v231.backend.model.SubUser;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemShoppingListRepository extends JpaRepository<ItemShoppingList, Integer> {

    Optional<ItemShoppingList> findByItemNameAndShoppingList_ShoppingListIdAndSubUserAccessLevel(
            String name, int shoppingListId, boolean subUserAccessLevel);
}
