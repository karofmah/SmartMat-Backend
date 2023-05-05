package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.repository.SubUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Class to manage SubUser objects.
 */
@Service
public class SubUserServices {

    private final SubUserRepository subUserRepo;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the repositories to use for database access.
     */
    @Autowired
    public SubUserServices(SubUserRepository subUserRepository) {
        this.subUserRepo = subUserRepository;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to get sub-users by email.
     *
     * @param email email
     * @return the sub-users as dto objects
     */
    public List<SubUserDto> getSubUsersByMaster(String email) {
        return subUserRepo
                .findAllByUserEmail(email)
                .stream()
                .map(obj -> mapper.map(obj, SubUserDto.class))
                .toList();
    }

    /**
     * Method to get sub-user by id.
     *
     * @param subUserId the id
     * @return the sub-user as a dto object
     */
    public SubUserDto getSubUser(int subUserId) {
        Optional<SubUser> subUser = subUserRepo.findById(subUserId);
        return mapper.map(subUser.get(), SubUserDto.class);
    }

    /**
     * Method to save a new sub-user.
     *
     * @param dto the new subuser
     * @return true if the sub-user is saved.
     */
    public boolean saveSubUser(SubUserCreationDto dto) {
        try {
            SubUser subUser = mapper.map(dto, SubUser.class);
            subUserRepo.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete a sub-user.
     *
     * @param subUserId the subuser id
     * @return true if the sub-user is deleted.
     */
    public boolean deleteSubUser(int subUserId) {
        try {
            subUserRepo.deleteById(subUserId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to update a sub-user.
     *
     * @param dto the new information about the sub-user
     * @return true if the sub-user is updated.
     */
    public boolean updateSubUser(SubUserDto dto) {
        try {
            SubUser subUser = mapper.map(dto, SubUser.class);
            subUser.setUser(subUserRepo.findById(dto.getSubUserId()).get().getUser());
            subUserRepo.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to validate pincode. Checks if the pincode from client matches subuser´s pincode
     *
     * @param dto the pincode with subuser
     * @return true if the pincode is correct
     */
    public boolean pinCodeValid(SubUserValidationDto dto){
        try{
            SubUser subUser = subUserRepo.findById(dto.getSubUserId()).get();
            return subUser.getPinCode() == dto.getPinCode();
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to check is a sub-user exist.
     *
     * @param email the master email
     * @param name the subuser name
     * @return true if the sub-user is exists.
     */
    public boolean subUserExists(String email, String name) {
        return subUserRepo.findByUserEmailAndName(email ,name).isPresent();
    }

    /**
     * Method to check is a sub-user not exist in database.
     *
     * @param subUserId the subuser id
     * @return true if the sub-user is exists.
     */
    public boolean subUserNotExists(int subUserId) {
        return !subUserRepo.existsBySubUserId(subUserId);
    }

    /**
     * Gets the email of a sub user
     *
     * @param subUserId the sub user ID
     * @return the master user
     */
    public String getMasterUserEmail(int subUserId) {
        Optional<SubUser> subUser = subUserRepo.findById(subUserId);
        return subUser.map(user -> user.getUser().getEmail()).orElse(null);
    }

    /**
     * Gets the access level of a sub user
     *
     * @param subUserId the sub user ID
     * @return the access level
     */
    public boolean getAccessLevel(int subUserId) {
        Optional<SubUser> subUser = subUserRepo.findById(subUserId);
        return subUser.map(SubUser::isAccessLevel).orElse(false);
    }
}