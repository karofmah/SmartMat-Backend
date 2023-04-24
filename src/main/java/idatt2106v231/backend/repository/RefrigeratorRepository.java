package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Refrigerator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefrigeratorRepository extends CrudRepository<Refrigerator, Integer> {

    Optional<Refrigerator> findByUserEmail(String email);
}
