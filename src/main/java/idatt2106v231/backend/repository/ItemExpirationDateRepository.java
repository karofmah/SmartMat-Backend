package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.ItemExpirationDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemExpirationDateRepository extends JpaRepository<ItemExpirationDate, Integer> {
    Optional<ItemExpirationDate> findTopByItemRefrigerator_ItemRefrigeratorIdOrderByDate(int id);

    List<ItemExpirationDate> findAllByItemRefrigerator_RefrigeratorRefrigeratorIdAndDateGreaterThanAndDateLessThanEqual(int id, Date start, Date end);

    List<ItemExpirationDate> findAllByItemRefrigerator_RefrigeratorRefrigeratorIdOrderByDate(int id);
}