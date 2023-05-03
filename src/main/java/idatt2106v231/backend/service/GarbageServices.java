package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.GarbageRepository;
import idatt2106v231.backend.repository.ItemRefrigeratorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;

/**
 * Class to manage Garbage objects.
 */
@Service
public class GarbageServices {

    private ItemRefrigeratorRepository itemRefRepo;
    private GarbageRepository garbRepo;

    private AiServices aiServices;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the itemRefrigerator repository to use for database access.
     *
     * @param itemRefRepo the itemRefrigerator repository to use
     */
    @Autowired
    public void setItemRefRepo(ItemRefrigeratorRepository itemRefRepo) {
        this.itemRefRepo = itemRefRepo;
    }

    /**
     * Sets the garbage repository to use for database access.
     *
     * @param garbRepo the garbage repository to use
     */
    @Autowired
    public void setGarbRepo(GarbageRepository garbRepo) {
        this.garbRepo = garbRepo;
    }

    @Autowired
    public void setAiServices(AiServices aiServices) {
        this.aiServices = aiServices;
    }

    /**
     * Method to add waste in the garbage table in the database.
     *
     * @param itemRefDto the garbage
     * @return true if the item is added to garbage
     */
    public boolean addToGarbage(EditItemInRefrigeratorDto itemRefDto){
        try {
            double amount = getCorrectAmount(itemRefDto);

            if (amount == 0){
                return false;
            }
            System.out.println(amount + " riktig amount i kilo");

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

    //se på hvordan man håndterer amount hvis man skal fjerne fra to forskjellige datovarer

    /**
     * Method to get the correct amount to add to garbage, in kg.
     *
     * @param itemRefDto information about the item
     * @return the amount in kg
     */
    private double getCorrectAmount(EditItemInRefrigeratorDto itemRefDto){
        ItemRefrigerator itemRef = itemRefRepo
                .findByItemNameAndRefrigeratorRefrigeratorId(itemRefDto.getItemName(), itemRefDto.getRefrigeratorId())
                .get();

        double amount;
        double amountInRefrigerator;


        if (itemRefDto.getMeasurementType().equals(Measurement.UNIT) && itemRef.getMeasurementType().equals(Measurement.UNIT)){

            //assert that the user does not add more food in the garbage then there is in the refrigerator.
            if(itemRef.getAmount() < itemRefDto.getAmount()){
                return changeFromUnitToKG(itemRef.getAmount(), itemRefDto.getItemName());
            }
            else{
                return changeFromUnitToKG(itemRefDto.getAmount(), itemRefDto.getItemName());
            }
        }

        amount = changeMeasurementTypeToKG(itemRefDto.getAmount(), itemRefDto.getMeasurementType(), itemRefDto.getItemName());
        amountInRefrigerator = changeMeasurementTypeToKG(itemRef.getAmount(), itemRef.getMeasurementType(), itemRef.getItem().getName());


        //assert that the user does not add more food in the garbage then there is in the refrigerator.
        if (amount > amountInRefrigerator){
           return amountInRefrigerator;
        }
        else{
            return amount;
        }
    }

    /**
     * Method to change amount from units to kg, by making an AI call.
     *
     * @param amount the amount in units
     * @param name the name of the item
     * @return the amount in kg
     */
    private double changeFromUnitToKG(double amount, String name){
       try {
           String query = "How much does one " + name + " weigh in gram roughly? Answer with one word as a number.";

           String response = aiServices.getChatCompletion(query).trim();

           StringBuilder sb = new StringBuilder();

           for (int i = 0; i < response.length(); i++) {
               char c = response.charAt(i);

               if (Character.isDigit(c)) {
                   sb.append(c);
               }
           }

           double numericString = Double.parseDouble(sb.toString());

           return numericString * amount / 1000;
       }catch (Exception e){
           return 0;
       }

    }

    private double changeMeasurementType(double amount, Measurement actualMeasurement, Measurement wantedMeasurement){

        double wantedAmount = 0;// = changeMeasurementTypeToKG(amount, actualMeasurement);

        if (wantedMeasurement == Measurement.KG || wantedMeasurement == Measurement.L) {
            return wantedAmount;
        }
        else if (wantedMeasurement == Measurement.DL) {
            return wantedAmount * 10;
        }
        else {
           return wantedAmount * 1000;
        }
    }

    /**
     * Method to change the amount to KG, if the amount is measured in units, a call to AI is made.
     *
     * @param amount the amount
     * @param measurement the measurement currently used
     * @param name the name of the item
     * @return the amount in kg.
     */
    private double changeMeasurementTypeToKG(double amount, Measurement measurement, String name){
        if (measurement == Measurement.UNIT){
            return changeFromUnitToKG(amount, name);
        }
        else if (measurement == Measurement.KG || measurement == Measurement.L)  {
            return amount;
        }
        if (measurement == Measurement.DL){
            return amount / 10;
        }
        if (measurement == Measurement.G){
            return amount / 1000;
        }
        return 0;
    }
}
