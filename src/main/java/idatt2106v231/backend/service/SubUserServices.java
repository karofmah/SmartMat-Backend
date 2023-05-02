package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
import idatt2106v231.backend.dto.user.UserDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.model.User;
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

    private SubUserRepository subUserRepo;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the subuser repository to use for database access.
     *
     * @param subUserRepo the
     */
    @Autowired
    public void setSubUserRepo(SubUserRepository subUserRepo) {
        this.subUserRepo = subUserRepo;
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

    public User getMasterUser(int subUserId) {
        Optional<SubUser> subUser = subUserRepository.findById(subUserId);
        return subUser.map(SubUser::getUser).orElse(null);
    }
}