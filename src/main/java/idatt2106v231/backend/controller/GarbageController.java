package idatt2106v231.backend.controller;

import idatt2106v231.backend.dto.garbage.GarbageDto;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.repository.GarbageRepository;
import idatt2106v231.backend.service.GarbageServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/garbages")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Garbage API", description = "API for managing garbages")
public class GarbageController {

    private final Logger logger = LoggerFactory.getLogger(GarbageController.class);

    private GarbageServices garbageServices;

    @Autowired
    public void setGarbageServices(GarbageServices garbageServices) {
        this.garbageServices = garbageServices;
    }

    @GetMapping("/garbage/totalAmountYear")
    public ResponseEntity<Object> calculateTotalAmountByIdYear(@RequestBody GarbageDto garbageDto) {

        ResponseEntity <Object> response;

        if(garbageDto.getRefrigeratorId()<=0 || garbageDto.getYear()<=0){
            response =  new ResponseEntity<>("Data is not specified", HttpStatus.BAD_REQUEST);
        }
        else if (!garbageServices.checkIfGarbagesExists()) {
            response = new ResponseEntity<>("There are no garbages registered in database", HttpStatus.NOT_FOUND);
        }else if (garbageServices.calculateTotalAmount(garbageDto.getRefrigeratorId(),garbageDto.getYear())==-1){
            response = new ResponseEntity<>("Total amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        else {
            System.out.println(garbageServices.calculateTotalAmount(garbageDto.getRefrigeratorId(), garbageDto.getYear()));
            response = new ResponseEntity<>(garbageServices.calculateTotalAmount(garbageDto.getRefrigeratorId(), garbageDto.getYear()), HttpStatus.OK);
        }
        logger.info(String.valueOf(response.getBody()));
        return response;
    }

}

