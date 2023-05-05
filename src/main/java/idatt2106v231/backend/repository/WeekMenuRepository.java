package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.WeeklyMenu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeekMenuRepository extends CrudRepository<WeeklyMenu, Integer> {

    Optional<WeeklyMenu> findByUserEmail(String email);

}