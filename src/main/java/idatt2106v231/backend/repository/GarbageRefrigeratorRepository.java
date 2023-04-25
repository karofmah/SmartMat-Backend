package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.GarbageRefrigerator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GarbageRefrigeratorRepository extends CrudRepository<GarbageRefrigerator, Integer> {

}
