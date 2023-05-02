package idatt2106v231.backend.service;

import idatt2106v231.backend.repository.GarbageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GarbageServices {

    private GarbageRepository garbageRepository;

    @Autowired
    public void setGarbageRepository(GarbageRepository garbageRepository) {
        this.garbageRepository = garbageRepository;
    }

    public int calculateAverageAmount(){
        try{
            return garbageRepository.averageAmount();
        }catch (Exception e){
            return -1;
        }
    }
    public boolean checkIfGarbagesExists(){
        return !garbageRepository.findAll().isEmpty();
    }


}
