package idatt2106v231.backend.controller;

import idatt2106v231.backend.service.GarbageServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/garbages")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Garbage API", description = "API for managing garbages")
public class GarbageController {

    private GarbageServices garbageServices;

    @Autowired
    public void setGarbageServices(GarbageServices garbageServices) {
        this.garbageServices = garbageServices;
    }

    @GetMapping("/averageAmount")
    public ResponseEntity<Object> calculateAverageAmount(){
       if(!garbageServices.checkIfGarbagesExists()){
            return new ResponseEntity<>("There are no garbages registered in database", HttpStatus.NOT_FOUND);
        }
        else if(garbageServices.calculateAverageAmount()==-1)
            return new ResponseEntity<>("Average amount of garbage can not be calculated", HttpStatus.INTERNAL_SERVER_ERROR);

        else {
            return new ResponseEntity<>(garbageServices.calculateAverageAmount(), HttpStatus.OK);
        }
    }
}
