package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Category findDistinctByDescription(String description);


}
