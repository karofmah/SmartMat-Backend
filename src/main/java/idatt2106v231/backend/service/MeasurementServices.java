package idatt2106v231.backend.service;

import idatt2106v231.backend.enums.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to change measurement types
 */
@Service
public class MeasurementServices {

    private final AiServices aiServices;

    /**
     * Constructor which sets the AiServices.
     */
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
            if (response.startsWith("ERROR: ")) throw new Exception();

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
     * @param amount the current amount
     * @return the amount in kg.
     */
    private double changeAmountToKG(double amount, Measurement currentMeasurement, String itemName){
        if (currentMeasurement.equals(Measurement.UNIT)){
            return changeAmountFromUnitToKG(amount, itemName);
        }
        else if (currentMeasurement.equals(Measurement.KG)
                || currentMeasurement.equals(Measurement.L)){
            return amount;
        }
        if (currentMeasurement.equals(Measurement.DL)){
            return amount / 10;
        }
        if (currentMeasurement.equals(Measurement.G)){
            return amount / 1000;
        }
        return 0;
    }

    /**
     * Method to change the amount of an item in the refrigerator to a specified measurement,
     * if the amount is measured in units, a call to AI is made.
     *
     * @param amount the current amount
     * @param wantedMeasurement the new measurement
     * @return the amount in wantedMeasurement.
     */
    public double changeAmountToWantedMeasurement(double amount, Measurement currentMeasurement, Measurement wantedMeasurement, String name){

        if (currentMeasurement.equals(wantedMeasurement)) return amount;

        if (wantedMeasurement.equals(Measurement.KG) || wantedMeasurement.equals(Measurement.L)) {
            return changeAmountToKG(amount, currentMeasurement, name);
        }
        else if (wantedMeasurement.equals(Measurement.G)) {
            return changeAmountToKG(amount, currentMeasurement, name) * 1000;
        }
        else if (wantedMeasurement.equals(Measurement.DL)){
            return changeAmountToKG(amount, currentMeasurement, name) * 10;
        }
        else if (wantedMeasurement.equals(Measurement.UNIT)){
            return changeAmountToUnit(amount, currentMeasurement, name);
        }
        else {
            return 0;
        }
    }

    /**
     * Method to change the amount of an item in the refrigerator to unit,
     * a call to AI is made.
     *
     * @param amount the amount to change
     * @return the amount in unit.
     */
    private double changeAmountToUnit(double amount, Measurement currentMeasurement, String itemName){
        double unit = changeAmountFromUnitToKG(1, itemName);

        if (currentMeasurement.equals(Measurement.KG) ||
                currentMeasurement.equals(Measurement.L)){
            return amount / unit;
        }
        else if (currentMeasurement.equals(Measurement.G)){
            return amount / 1000 / unit;
        }
        else if (currentMeasurement.equals(Measurement.DL)){
            return amount / 10 / unit;
        }
        else {
            return -1;
        }
    }
}