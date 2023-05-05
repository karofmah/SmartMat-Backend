package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.ItemExpirationDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemExpirationDateRepository extends JpaRepository<ItemExpirationDate, Integer> {

    List<ItemExpirationDate> findAllByItemRefrigerator_RefrigeratorRefrigeratorIdAndDateGreaterThanAndDateLessThanEqual(int id, Date start, Date end);

    List<ItemExpirationDate> findAllByItemRefrigerator_RefrigeratorRefrigeratorIdOrderByDate(int id);

    boolean existsByItemExpirationDateId(int id);
}