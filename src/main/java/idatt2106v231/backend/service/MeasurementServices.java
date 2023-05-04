package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementServices {

    private final AiServices aiServices;

    @Autowired
    public MeasurementServices(AiServices aiServices){
        this.aiServices = aiServices;
    }

    /**
     * Method to change amount from units to kg, by making an AI call.
     *
     * @param amount the amount in units
     * @param name the name of the item
     * @return the amount in kg
     */
    private double changeAmountFromUnitToKG(double amount, String name){
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

    /**
     * Method to change the amount of an item in the refrigerator to KG,
     * if the amount is measured in units, a call to AI is made.
     *
     * @param itemRefDto the item in refrigerator to change amount
     * @return the amount in kg.
     */
    private double changeAmountToKG(EditItemInRefrigeratorDto itemRefDto){
        if (itemRefDto.getMeasurementType().equals(Measurement.UNIT)){
            return changeAmountFromUnitToKG(itemRefDto.getAmount(), itemRefDto.getItemName());
        }
        else if (itemRefDto.getMeasurementType().equals(Measurement.KG)
                || itemRefDto.getMeasurementType().equals(Measurement.L)){
            return itemRefDto.getAmount();
        }
        if (itemRefDto.getMeasurementType() == Measurement.DL){
            return itemRefDto.getAmount() / 10;
        }
        if (itemRefDto.getMeasurementType() == Measurement.G){
            return itemRefDto.getAmount() / 1000;
        }
        return 0;
    }

    /**
     * Method to change the amount of an item in the refrigerator to a specified measurement,
     * if the amount is measured in units, a call to AI is made.
     *
     * @param itemRefDto information about the item in the refrigerator
     * @param wantedMeasurement the new measurement
     * @return the amount in wantedMeasurement.
     */
    public double changeAmountToWantedMeasurement(EditItemInRefrigeratorDto itemRefDto, Measurement wantedMeasurement){
        Measurement actualMeasurement = itemRefDto.getMeasurementType();

        if (actualMeasurement.equals(wantedMeasurement)) return itemRefDto.getAmount();

        if (wantedMeasurement.equals(Measurement.KG) || wantedMeasurement.equals(Measurement.L)) {
            return changeAmountToKG(itemRefDto);
        }
        else if (wantedMeasurement.equals(Measurement.G)) {
            return changeAmountToKG(itemRefDto) * 1000;
        }
        else if (wantedMeasurement.equals(Measurement.DL)){
            return changeAmountToKG(itemRefDto) * 10;
        }
        else if (wantedMeasurement.equals(Measurement.UNIT)){
            return changeAmountToUnit(itemRefDto);
        }
        else {
            return 0;
        }
    }

    /**
     * Method to change the amount of an item in the refrigerator to unit,
     * a call to AI is made.
     *
     * @param itemRefDto information about the item in the refrigerator
     * @return the amount in unit.
     */
    private double changeAmountToUnit(EditItemInRefrigeratorDto itemRefDto){
        double unit = changeAmountFromUnitToKG(1, itemRefDto.getItemName());

        if (itemRefDto.getMeasurementType().equals(Measurement.KG) ||
                itemRefDto.getMeasurementType().equals(Measurement.L)){
            return itemRefDto.getAmount() / unit;
        }
        else if (itemRefDto.getMeasurementType().equals(Measurement.G)){
            return itemRefDto.getAmount() / 1000 / unit;
        }
        else if (itemRefDto.getMeasurementType().equals(Measurement.DL)){
            return itemRefDto.getAmount() / 10 / unit;
        }
        else {
            return -1;
        }
    }
}