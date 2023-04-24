package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.OpenAiKey;
import idatt2106v231.backend.model.Refrigerator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OpenAiKeyRepository extends CrudRepository<OpenAiKey, Integer> {

    Optional<OpenAiKey> findFirstByOrderByIdDesc();
}
