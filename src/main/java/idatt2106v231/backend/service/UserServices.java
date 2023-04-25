package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.dto.user.UserCreationDto;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.model.User;
>>>>>>> src/main/java/idatt2106v231/backend/service/UserServices.java
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to handle User objects.
 */
@Service
public class UserServices {

    private UserRepository userRepo;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the user repository to use for database access.
     *
     * @param userRepo the user repository to use
     */
    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Method to get a user with a specified email address.
     *
     * @param email the email address of the user to retrieve
     * @return the user
     */
    public UserDto getUser(String email) {
        try{
            return mapper.map(userRepo.findByEmail(email), UserDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to check if a user exist.
     *
     * @param email the email address of the user
     * @return true if the user exist
     */
    public boolean checkIfUserExists(String email){
        return userRepo.findById(email).isPresent();
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