package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.dto.user.UserUpdateDto;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to manage User objects.
 */
@Service
public class UserServices {

    private final UserRepository userRepo;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the user repository to use for database access.
     */
    @Autowired
    public UserServices(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to get a user with a specified email address.
     *
     * @param email the email address of the user to retrieve
     * @return the user
     */
    public UserDto getUser(String email) {
        try{
            User user = userRepo
                    .findByEmail(email)
                    .get();
            return mapper.map(user, UserDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method for updating a user.
     *
     * @param userUpdateDto the new information about the user as a dto
     * @return true if the user is updated
     */
    public boolean updateUser(UserUpdateDto userUpdateDto) {
        try {
            User user = userRepo.findByEmail(userUpdateDto.getEmail()).get();
            user.setFirstName(userUpdateDto.getFirstName());
            user.setLastName(userUpdateDto.getLastName());
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
            user.setHousehold(userUpdateDto.getHousehold());

            userRepo.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to check if a user not exist.
     *
     * @param email the email address of the user
     * @return true if the user not exist
     */
    public boolean userNotExists(String email){
        return !userRepo.existsByEmail(email);
    }
}