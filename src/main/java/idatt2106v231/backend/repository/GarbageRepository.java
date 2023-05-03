package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Garbage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.lang.management.OperatingSystemMXBean;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface GarbageRepository extends CrudRepository<Garbage, Integer> {
    List<Garbage> findAll();

    Optional<Garbage> findByRefrigeratorRefrigeratorIdAndDate(int refrigeratorId, YearMonth date);

    List<Garbage> findAllByRefrigeratorRefrigeratorIdAndDate(int refrigeratorId, YearMonth date);

    List<Garbage> findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(int refrigeratorId, YearMonth date, YearMonth date2);
   // List<Garbage> findAllByRefrigeratorRefrigeratorIdAndDate_Year(int refrigeratorId, Year year);


}
