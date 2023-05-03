package idatt2106v231.backend.service;

import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.repository.GarbageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class GarbageServices {

    private GarbageRepository garbageRepository;

    @Autowired
    public void setGarbageRepository(GarbageRepository garbageRepository) {
        this.garbageRepository = garbageRepository;
    }

    public int calculateTotalAmount(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbageRepository.findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(id, YearMonth.of(year, 1),YearMonth.of(year,12));
            int totalAmount = 0;
            for (Garbage garbage : garbageList) {
                totalAmount += garbage.getAmount();
            }
            return totalAmount;
        } catch (Exception e){
            return -1;
        }
    }

    public boolean checkIfGarbagesExists(){
        return !garbageRepository.findAll().isEmpty();
    }


}
