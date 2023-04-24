package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private UserRepository userRepository;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the user repository to use for database access.
     *
     * @param userRepository the user repository to use.
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * This method retrieves a user with a specified email address from the database.
     *
     * @param email the email address of the user to retrieve.
     * @return an Optional object containing the user with the specified email address, or an empty Optional object if the user does not exist in the database.
     */
    public UserCreationDto getUser(String email) {
        try{
            Optional<User> user=userRepository.findByEmail(email);

            if(user.isPresent()) {
                _logger.info("User was retrieved successfully!");
                return mapper.map(user.get(), UserCreationDto.class);
            }
            else{
                throw new IllegalArgumentException();
            }
        }catch (IllegalArgumentException e){
            _logger.error("Failed to get user for email " + email,e);
            return null;
        }
    }
    public boolean checkIfUserExists(String email){
        return userRepository.findById(email).isPresent();
    }

    /**
     * Method for updating a user
     * @param email the email of the user
     * @param userUpdateDto the DTO of the updated user
     * @return if the user is updated
     */
    public boolean updateUser(String email, UserUpdateDto userUpdateDto) {
        try {
            Optional<User> userData = userRepository.findByEmail(email);

            if (userData.isPresent()) {
                User _user = userData.get();
                _user.setFirstName(userUpdateDto.getFirstName());
                _user.setLastName(userUpdateDto.getLastName());
                _user.setPhoneNumber(userUpdateDto.getPhoneNumber());
                _user.setHousehold(userUpdateDto.getHousehold());
                userRepository.save(_user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            _logger.error("Failed update user with email " + email, e);
            return false;
        }
    }
}


