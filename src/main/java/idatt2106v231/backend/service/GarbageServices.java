package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.repository.GarbageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.YearMonth;
import java.util.List;

/**
 * Class to manage Garbage objects.
 */


@Service
public class GarbageServices {

    private final GarbageRepository garbRepo;

    private final MeasurementServices measurementServices;

    private final ModelMapper mapper;

    @Autowired
    public GarbageServices(GarbageRepository garbRepo, MeasurementServices measurementServices) {
        this.garbRepo = garbRepo;
        this.measurementServices = measurementServices;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to add garbage table in the database.
     *
     * @param itemRefDto the garbage
     * @return true if the item is added to garbage
     */
    public boolean addToGarbage(EditItemInRefrigeratorDto itemRefDto){
        try {
            double amount = measurementServices.changeAmountToWantedMeasurement(itemRefDto, Measurement.KG);

            Optional<Garbage> garbage = garbRepo.findByRefrigeratorRefrigeratorIdAndDate(itemRefDto.getRefrigeratorId(), YearMonth.now());
            Garbage gar;

            if (garbage.isPresent()){
                gar = garbage.get();
                gar.updateAmount(amount);
            }
            else{
                gar = mapper.map(itemRefDto, Garbage.class);
                gar.setAmount(amount);
                gar.setDate(YearMonth.now());
            }

            garbRepo.save(gar);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public int calculateTotalAmount(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbRepo.findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(id, YearMonth.of(year, 1),YearMonth.of(year,12));
            int totalAmount = 0;
            for (Garbage garbage : garbageList) {
                totalAmount += garbage.getAmount();
            }
            return totalAmount;
        } catch (Exception e){
            return -1;
        }
    }

    public boolean refrigeratorIsEmpty(int id){
        return garbRepo.findAllByRefrigeratorRefrigeratorId(id).isEmpty();
    }
}