package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.SubUserRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
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

    private final UserRepository userRepo;

    private final ModelMapper mapper;

    @Autowired
    public SubUserServices(SubUserRepository subUserRepository, UserRepository userRepository) {
        this.subUserRepo = subUserRepository;
        this.userRepo = userRepository;
        this.mapper = new ModelMapper();
        TypeMap<SubUser, SubUserDto> propertyMapper = mapper.createTypeMap(SubUser.class, SubUserDto.class);
       // propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getUser().getEmail(), SubUserDto::setMasterUser));

    }

    public List<SubUserDto> getSubUsersByMaster(String email) { //sjekk
        return subUserRepo.findAllByUserEmail(email).stream()
                .map(obj -> mapper.map(obj, SubUserDto.class)).toList();
    }

    public SubUserDto getSubUser(int subUserId) { //sjekk
        Optional<SubUser> subUser = subUserRepo.findById(subUserId);
        return mapper.map(subUser.get(), SubUserDto.class);
    }

    public boolean saveSubUser(SubUserCreationDto subUserCreationDto) {
        try {
            SubUser subUser = mapper.map(subUserCreationDto, SubUser.class);
            subUserRepo.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSubUser(int subUserId) {
        try {
            subUserRepo.deleteById(subUserId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateSubUser(SubUserDto subDto) {
        try {
            SubUser subUser = mapper.map(subDto, SubUser.class);
            subUser.setUser(subUserRepo.findById(subDto.getSubUserId()).get().getUser());
            subUserRepo.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean pinCodeValid(SubUserValidationDto subDto){
        try{
            SubUser subUser = subUserRepo.findById(subDto.getSubUserId()).get();
            return subUser.getPinCode() == subDto.getPinCode();
        }catch (Exception e){
            return false;
        }
    }

    public boolean subUserExists(String name, String email) {
        return subUserRepo.findByUserEmailAndName(email, name).isPresent();
    }

    public boolean subUserExists(int subUserId) {
        return subUserRepo.existsBySubUserId(subUserId);
    }

    /**
     * Gets the master user of a sub user
     *
     * @param subUserId the sub user ID
     * @return the master user
     */
    public User getMasterUser(int subUserId) {
        Optional<SubUser> subUser = subUserRepo.findById(subUserId);
        return subUser.map(SubUser::getUser).orElse(null);
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