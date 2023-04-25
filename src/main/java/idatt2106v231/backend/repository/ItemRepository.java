package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

    Optional<Item> findByName(String name);

    List<Item> findAllByCategoryCategoryId(int categoryId);

    List<Item> findAll();
}