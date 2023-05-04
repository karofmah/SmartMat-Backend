package idatt2106v231.backend.service;

import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.repository.GarbageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
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


    public int[] calculateAmountEachMonth(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbageRepository.findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(id,YearMonth.of(year,1),YearMonth.of(year,12));
            int[] amountEachMonth=new int[12];
            for (Garbage garbage : garbageList) {
                amountEachMonth[garbage.getDate().getMonthValue()-1]+=garbage.getAmount();
            }
            return amountEachMonth;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public int calculateAverageAmount(int id, int year){
        try {
            List<Garbage> totalGarbageList =
                    garbageRepository.findAllByDateIsBetween(YearMonth.of(year,1),YearMonth.of(year,12));
            int totalAmount=0;
            int [] totalAmountList=new int [totalGarbageList.size()];
            int size=0;
            for (Garbage garbage : totalGarbageList) {
                if(garbage.getRefrigerator().getRefrigeratorId()!=id){
                    totalAmountList[garbage.getRefrigerator().getRefrigeratorId()]+=garbage.getAmount();
                }
            }
            for (int amount:totalAmountList) {
                if(amount>0){
                    size++;
                }
                totalAmount+=amount;
            }

            return totalAmount/ size;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return -1;
        }
    }
    public boolean refrigeratorIsEmpty(int id) {
        return garbageRepository.findAllByRefrigeratorRefrigeratorId(id).isEmpty();
    }
}
