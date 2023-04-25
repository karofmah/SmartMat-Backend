package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class to manage User objects.
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
     * Method for updating a user
     *
     * @param userUpdateDto the new information about the user as a dto
     * @return true if the user is updated
     */
    public boolean updateUser(UserUpdateDto userUpdateDto) {
        try {
            Optional<User> userData = userRepo.findByEmail(userUpdateDto.getEmail());

            if (userData.isPresent()) {
                User _user = userData.get();
                _user.setFirstName(userUpdateDto.getFirstName());
                _user.setLastName(userUpdateDto.getLastName());
                _user.setPhoneNumber(userUpdateDto.getPhoneNumber());
                _user.setHousehold(userUpdateDto.getHousehold());
                userRepo.save(_user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
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

}