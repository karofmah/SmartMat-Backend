package idatt2106v231.backend.service;

import idatt2106v231.backend.BackendApplication;
import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes= BackendApplication.class)
class MeasurementServicesTest {

    @Autowired
    MeasurementServices measurementServices;

    @Test
    public void changeFromLToG() {
        double newAmount = measurementServices
                .changeAmountToWantedMeasurement(
                        2,
                        Measurement.L,
                        Measurement.G,
                        "milk"
                        );
        assertEquals(newAmount, 2000);
    }

    @Test
    public void changeFromKGToDL() {
        double newAmount = measurementServices
                .changeAmountToWantedMeasurement(
                        3,
                        Measurement.KG,
                        Measurement.DL,
                        "flour"
                );

        assertEquals(newAmount, 30);
    }

    @Test
    public void changeFromDLToG() {
        double newAmount = measurementServices
                .changeAmountToWantedMeasurement(
                        4,
                        Measurement.DL,
                        Measurement.G,
                        "pasta"
                );

        assertEquals(newAmount, 400);
    }

    @Test
    @Disabled("These tests require a private key and should not run in the pipeline")
    public void changeFromUNITToDL() {
        double newAmount = measurementServices
                .changeAmountToWantedMeasurement(
                        3.2,
                        Measurement.UNIT,
                        Measurement.DL,
                        "milk"
                );

        assertEquals(newAmount, 32);
    }

    @Test
    @Disabled("These tests require a private key and should not run in the pipeline")
    public void changeFromDLToUNIT() {

        double newAmount = measurementServices
                .changeAmountToWantedMeasurement(
                        2,
                        Measurement.DL,
                        Measurement.UNIT,
                        "orange juice"
                );

        assertEquals(newAmount, 0.2);
    }
}