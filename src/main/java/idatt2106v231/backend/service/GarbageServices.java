package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorRemovalDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.ItemExpirationDate;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.GarbageRepository;
import idatt2106v231.backend.repository.ItemExpirationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage Garbage objects.
 */
@Service
public class GarbageServices {

    private final GarbageRepository garbRepo;
    private final ItemExpirationDateRepository itemExpRepo;

    private final MeasurementServices measurementServices;

    /**
     * Constructor which sets the repositories to use for database access.
     */
    @Autowired
    public GarbageServices(GarbageRepository garbRepo, ItemExpirationDateRepository itemExpRepo,
                           MeasurementServices measurementServices) {
        this.garbRepo = garbRepo;
        this.itemExpRepo = itemExpRepo;
        this.measurementServices = measurementServices;
    }

    /**
     * Method to add garbage to the database.
     *
     * @param dto the garbage
     * @return true if the item is added to garbage
     */
    public boolean addToGarbage(ItemInRefrigeratorRemovalDto dto){
        try {
            ItemExpirationDate itemExp = itemExpRepo.findById(dto.getItemExpirationDateId()).get();
            ItemRefrigerator itemRef = itemExp.getItemRefrigerator();
            Garbage gar;

            double amount = measurementServices
                    .changeAmountToWantedMeasurement(
                            dto.getAmount(),
                            itemRef.getMeasurementType(),
                            Measurement.KG,
                            itemRef.getItem().getName()
                    );

            if(amount > itemExp.getAmount()){
                amount = itemExp.getAmount();
            }

            Optional<Garbage> garbage = garbRepo
                    .findByRefrigeratorRefrigeratorIdAndDate(
                            itemRef.getRefrigerator().getRefrigeratorId(),
                            YearMonth.now()
                    );

            if (garbage.isPresent()){
                gar = garbage.get();
                gar.updateAmount(amount);
            }
            else{
                gar = Garbage.builder()
                        .refrigerator(itemRef.getRefrigerator())
                        .amount(amount)
                        .date(YearMonth.now())
                        .build();
            }
            garbRepo.save(gar);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to get calculated amount of garbage thrown in a year by a refrigerator.
     *
     * @param refrigeratorId the refrigerator id
     * @param year the year
     * @return the amount
     */
    public double calculateTotalAmountByYearAndRefrigerator(int refrigeratorId, int year){
        try {
            Optional<Double> totalAmount = garbRepo
                    .findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(
                            refrigeratorId,
                            YearMonth.of(year, 1),
                            YearMonth.of(year,12)
                    )
                    .stream()
                    .map(Garbage::getAmount)
                    .reduce(Double::sum)
                    ;

            if (totalAmount.isPresent()){
                return totalAmount.get();
            }

            return 0;
        } catch (Exception e){
            return -1;
        }
    }

    /**
     * Method to get a list of calculated amount of garbage thrown each month in a year by a refrigerator.
     *
     * @param refrigeratorId the refrigerator id
     * @param year the year
     * @return the amount
     */
    public double[] calculateTotalAmountEachMonthByYearAndRefrigerator(int refrigeratorId, int year){
        try {
            List<Garbage> garbageList = garbRepo
                    .findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(
                            refrigeratorId,
                            YearMonth.of(year,1),
                            YearMonth.of(year,12)
                    );

            double[] amountEachMonth = new double[12];

            for (Garbage garbage : garbageList) {
                amountEachMonth[garbage.getDate().getMonthValue()-1] += garbage.getAmount();
            }
            return amountEachMonth;
        } catch (Exception e){
            return null;
        }
    }


    /**
     * Method to get average amount of garbage thrown in a year without a refrigerator.
     *
     * @param refrigeratorId the refrigerator refrigeratorId
     * @param year the year
     * @return the amount
     */
    public double calculateAverageAmountByYearWithoutRefrigerator(int refrigeratorId, int year){
        try {
            List<Garbage> garbageList =
                    garbRepo.findAllByRefrigeratorRefrigeratorIdNotAndDateIsBetween(
                            refrigeratorId,
                            YearMonth.of(year,1),
                            YearMonth.of(year,12)
                    );

            if (garbageList.isEmpty()){
                return 0;
            }


            HashMap<Integer, Double> map = new HashMap<>();

            for (Garbage garbage : garbageList) {
                int key = garbage.getRefrigerator().getRefrigeratorId();

                if (!map.containsKey(key)){
                    map.put(key, garbage.getAmount());
                }
                else {
                    double updatedValue = map.get(key) + garbage.getAmount();
                    map.put(key, updatedValue);
                }
            }

            double totalAmount = map.values()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            double averageAmount = totalAmount / map.size();

            return Math.round(averageAmount * 100.0) / 100.0;
        } catch (Exception e){
            return -1;
        }
    }

    /**
     * Method to get average amount of garbage thrown each month in a year without a refrigerator.
     *
     * @param refrigeratorId the refrigerator refrigeratorId
     * @param year the year
     * @return the amount
     */
    public double[] calculateAverageAmountEachMonthByYearAndRefrigerator(int refrigeratorId, int year){
        try {
            List<Garbage> garbageList =
                    garbRepo.findAllByRefrigeratorRefrigeratorIdNotAndDateIsBetween(
                            refrigeratorId,
                            YearMonth.of(year,1),
                            YearMonth.of(year,12)
                    );

            double[] totalAmountEachMonth = new double[12];
            double[] averageAmountEachMonth = new double[12];
            double[] sizeArray = new double[12];

            //Initialize array with array list that will represent
            //refrigerator IDs per month
            ArrayList<Integer>[] idsEachMonth = new ArrayList[12];
            for (int i = 0; i < idsEachMonth.length; i++) {
                idsEachMonth[i] = new ArrayList<>();
            }

            //Iterate through all garbages, add amount for each month to
            //corresponding index.
            //Fill array lists with all refrigerator ID
            //divided into months (one list per month)
            for (Garbage garbage : garbageList) {
                totalAmountEachMonth[garbage.getDate().getMonthValue()-1] += garbage.getAmount();
                idsEachMonth[garbage.getDate().getMonthValue()-1].add(garbage.getRefrigerator().getRefrigeratorId());
            }

            //Remove duplicates of refrigerator IDs
            for (int i = 0; i < 12; i++) {
                sizeArray[i] = idsEachMonth[i].stream().distinct().toList().size();
            }

            //Calculate average amount of garbage per month
            //using total amount of garbage that month and the
            //number of unique users that have thrown garbage that month
            for (int i = 0; i < totalAmountEachMonth.length; i++) {
                if(sizeArray[i] != 0){
                    averageAmountEachMonth[i] = totalAmountEachMonth[i]/sizeArray[i];
                }
            }

            return averageAmountEachMonth;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Checks if a refrigerator has registered garbage in a specified year
     *
     * @param id the refrigerator id
     * @param year the year
     * @return true is a refrigerator has garbage
     */
    public boolean hasGarbageByYear(int id, int year) {
        return garbRepo.findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(
                id,
                YearMonth.of(year, 1),
                YearMonth.of(year, 12)
        ).isEmpty();
    }
}