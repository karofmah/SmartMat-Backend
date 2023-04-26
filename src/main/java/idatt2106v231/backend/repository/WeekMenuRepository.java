package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.WeeklyMenu;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WeekMenuRepository extends CrudRepository<WeeklyMenu, Integer> {

    Optional<WeeklyMenu> findByUserEmail(String email);

}
