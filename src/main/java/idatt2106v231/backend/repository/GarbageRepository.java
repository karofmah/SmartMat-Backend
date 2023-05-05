package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.Garbage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface GarbageRepository extends CrudRepository<Garbage, Integer> {

    List<Garbage> findAll();

    Optional<Garbage> findByRefrigeratorRefrigeratorIdAndDate(int refrigeratorId, YearMonth date);

    List<Garbage> findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(int refrigeratorId, YearMonth start, YearMonth end);

    List<Garbage> findAllByRefrigeratorRefrigeratorId(int refrigeratorId);

    List<Garbage> findAllByRefrigeratorRefrigeratorIdNotAndDateIsBetween(int refrigeratorId,YearMonth start,YearMonth end);
}