package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorRemovalDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.ItemExpirationDate;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.GarbageRepository;
import idatt2106v231.backend.repository.ItemExpirationDateRepository;
import idatt2106v231.backend.repository.ItemRefrigeratorRepository;
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
    private final ItemExpirationDateRepository itemExpRepo;

    private final MeasurementServices measurementServices;

    private final ModelMapper mapper;

    @Autowired
    public GarbageServices(GarbageRepository garbRepo, ItemExpirationDateRepository itemExpRepo,
                           MeasurementServices measurementServices) {
        this.garbRepo = garbRepo;
        this.itemExpRepo = itemExpRepo;
        this.measurementServices = measurementServices;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to add garbage table in the database.
     *
     * @param dto the garbage
     * @return true if the item is added to garbage
     */
    public boolean addToGarbage(ItemInRefrigeratorRemovalDto dto){
        try {
            ItemRefrigerator itemRef = itemExpRepo.findById(dto.getItemExpirationDateId()).get().getItemRefrigerator();
            Garbage gar;

            double amount = measurementServices
                    .changeAmountToWantedMeasurement(
                            dto.getAmount(),
                            itemRef.getMeasurementType(),
                            Measurement.KG,
                            itemRef.getItem().getName()
                    );

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