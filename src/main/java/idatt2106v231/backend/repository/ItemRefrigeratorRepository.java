package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.ItemRefrigerator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRefrigeratorRepository extends CrudRepository<ItemRefrigerator, Integer> {

    Optional<ItemRefrigerator> findByItemNameAndRefrigeratorRefrigeratorId(String itemName, int refrigeratorId);

    List<ItemRefrigerator> findAll();
}