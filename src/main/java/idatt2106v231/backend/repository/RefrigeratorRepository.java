package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.model.Refrigerator;
import idatt2106v231.backend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefrigeratorRepository extends CrudRepository<Refrigerator, Integer> {

    Optional<Refrigerator> findByUserEmail(String email);

    void deleteByUserEmail(String email);
}
