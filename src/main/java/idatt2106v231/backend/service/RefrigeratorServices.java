package idatt2106v231.backend.service;


import idatt2106v231.backend.dto.RefrigeratorDto;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.Refrigerator;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to handle Refrigerator objects
 */
@Service
public class RefrigeratorServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private RefrigeratorRepository refrigeratorRepository;

    private UserRepository userRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public void setRefrigeratorRepository(RefrigeratorRepository refrigeratorRepository) {
        this.refrigeratorRepository = refrigeratorRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Method to save a new refrigerator to database
     *
     * @param refrigerator the new refrigerator
     */

    public boolean saveRefrigerator(RefrigeratorDto refrigerator) {
        try {
            Refrigerator ref = mapper.map(refrigerator, Refrigerator.class);
            Optional<User> userOptional=userRepository.findByEmail(refrigerator.getUser().getEmail());
            userOptional.ifPresent(refrigerator::setUser);
            refrigeratorRepository.save(ref);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkIfRefrigeratorExists(User user){
        return refrigeratorRepository.findByUserEmail(user.getEmail()).isPresent();
    }
    public boolean checkIfRefrigeratorExists(int refrigeratorId){
        return refrigeratorRepository.findById(refrigeratorId).isPresent();
    }


    /**
     * Method to delete a refrigerator from database
     *
     * @param refrigeratorId the refrigerators id
     */
    public boolean deleteRefrigerator(int refrigeratorId) {
        try{
            refrigeratorRepository.deleteById(refrigeratorId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * Method to get a refrigerator by id
     *
     * @param id the refrigerators id
     */
    public void getRefrigerator(int id) {
    }

    /**
     * Method to get a refrigerator by user
     *
     * @param user the user
     */
    public void getRefrigeratorByUser(String user) {
    }

    /**
     * Method to get all refrigerators
     *
     */
    public void getAllRefrigerators() {
    }

    /**
     * Method to get all items in refrigerator
     *
     */
    public void getItemInRefrigerator(int id) {

    }

    /**
     * Method to get all items in refrigerator
     *
     */
    public void getItemsInRefrigerator(int id) {


    }
}