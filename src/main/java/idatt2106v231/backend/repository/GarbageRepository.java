package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Garbage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarbageRepository extends CrudRepository<Garbage, Integer> {

}
