package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Integer> {
    Optional<ShoppingList> findDistinctByUserEmail(String email);

    boolean existsShoppingListByShoppingListId(int id);
}