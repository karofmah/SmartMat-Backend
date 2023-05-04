package idatt2106v231.backend.service;

import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.repository.GarbageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
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


    public int[] calculateTotalAmountEachMonth(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbageRepository.findAllByRefrigeratorRefrigeratorIdAndDateIsBetween(id,YearMonth.of(year,1),YearMonth.of(year,12));
            int[] amountEachMonth=new int[12];
            for (Garbage garbage : garbageList) {
                amountEachMonth[garbage.getDate().getMonthValue()-1]+=garbage.getAmount();
            }
            return amountEachMonth;
        } catch (Exception e){
            return null;
        }
    }
    public int calculateAverageAmount(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbageRepository.findAllByRefrigeratorRefrigeratorIdNotAndDateIsBetween(id,YearMonth.of(year,1),YearMonth.of(year,12));
            int totalAmount=0;
            int [] totalAmountList=new int [garbageList.size()];
            int size=0;
            for (Garbage garbage : garbageList) {
                totalAmountList[garbage.getRefrigerator().getRefrigeratorId()]+=garbage.getAmount();
            }
            for (int amount:totalAmountList) {
                if(amount>0){
                    size++;
                }
                totalAmount+=amount;
            }

            return totalAmount/size;
        } catch (Exception e){
            return -1;
        }
    }
    public int[] calculateAverageAmountEachMonth(int id, int year){
        try {
            List<Garbage> garbageList =
                    garbageRepository.findAllByRefrigeratorRefrigeratorIdNotAndDateIsBetween(id,YearMonth.of(year,1),YearMonth.of(year,12));
            int[] totalAmountEachMonth=new int[12];
            int[] averageAmountEachMonth=new int[12];
            int[] sizeArray=new int[12];

            //Initialize array with array list that will represent
            //refrigerator IDs per month
            ArrayList<Integer>[] idsEachMonth=new ArrayList[12];
            for (int i = 0; i < idsEachMonth.length; i++) {
                idsEachMonth[i]=new ArrayList<>();
            }

            //Iterate through all garbages, add amount for each month to
            //corresponding index.
            //Fill array lists with all refrigerator ID
            //divided into months (one list per month)
            for (Garbage garbage : garbageList) {
                totalAmountEachMonth[garbage.getDate().getMonthValue()-1]+=garbage.getAmount();
                idsEachMonth[garbage.getDate().getMonthValue()-1].add(garbage.getRefrigerator().getRefrigeratorId());
            }

            //Remove duplicates of refrigerator IDs
            for (int i = 0; i < 12; i++) {
                sizeArray[i]=idsEachMonth[i].stream().distinct().toList().size();
            }

            //Calculate average amount of garbage per month
            //using total amount of garbage that month and the
            //number of unique users that have thrown garbage that month
            for (int i = 0; i < totalAmountEachMonth.length; i++) {
                if(sizeArray[i]!=0){
                    averageAmountEachMonth[i]=totalAmountEachMonth[i]/sizeArray[i];
                }
            }

            return averageAmountEachMonth;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public boolean refrigeratorIsEmpty(int id) {
        return garbageRepository.findAllByRefrigeratorRefrigeratorId(id).isEmpty();
    }
}
