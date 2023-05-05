package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Optional<Category> findByDescription(String description);

    List<Category> findAll();

    boolean existsCategoriesByCategoryId(int categoryId);

    boolean existsCategoriesByDescription(String description);
}