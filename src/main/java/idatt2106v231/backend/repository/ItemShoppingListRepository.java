package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemShoppingListRepository extends JpaRepository<ItemShoppingList, Integer> {
    List<ItemShoppingList> findAllByShoppingListShoppingListId(int id);
}
