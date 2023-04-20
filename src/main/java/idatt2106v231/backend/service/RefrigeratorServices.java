package idatt2106v231.backend.service;


import idatt2106v231.backend.dto.RefrigeratorDto;
import idatt2106v231.backend.model.Refrigerator;
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

    /*
    /**
     * Method to save a new refrigerator to database
     *
     * @param refrigeratorDto the new refrigerator
     */
    /*
    public void saveRefrigerator(RefrigeratorDto refrigerator) {
        try {
            Refrigerator ref=mapper.map(refrigerator,Refrigerator.class);
            ref.setUser(userRepository.findDistinctByEmail(refrigerator.getUser()));
            List<RefrigeratorDto> refrigerators = new ArrayList<>(refrigeratorRepository.findAll());

            for (RefrigeratorDto r : refrigerators) {
                if (Objects.equals(r.getUser(), refrigeratorDto.getUser())) {
                    _logger.info("Refrigerator already exists");
                    return null;
                }
            }

            user.setRole(Role.NORMAL_USER);

            _logger.info("User registered successfully");
            return userRepository.save(user);
        } catch (Exception e) {
            _logger.info("Error occurred while registering user: " + e.getMessage());
            return null;
        }
    }
*/
    /**
     * Method to delete a refrigerator from database
     *
     * @param refrigeratorId the refrigerators id
     */
    public void deleteRefrigerator(int refrigeratorId) {
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
    public void getItemsInRefrigerator(int id) {
    }
}