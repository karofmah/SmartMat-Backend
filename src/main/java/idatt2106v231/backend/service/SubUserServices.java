package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.model.SubUser;
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

    private SubUserRepository subUserRepository;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the subuser repository to use for database access.
     *
     * @param subUserRepository the
     */
    @Autowired
    public void setSubUserRepository(SubUserRepository subUserRepository) {
        this.subUserRepository = subUserRepository;
    }

    public List<SubUserDto> getSubUsersByMaster(String email) {
        return subUserRepository.findAllByUserEmail(email).stream()
                .map(obj -> mapper.map(obj, SubUserDto.class)).toList();
    }

    public SubUserDto getSubUser(int subUserId) {
        Optional<SubUser> subUser = subUserRepository.findById(subUserId);
        return mapper.map(subUser.get(), SubUserDto.class);
    }

    public boolean saveSubUser(SubUserDto subDto) {
        try {
            SubUser subUser = mapper.map(subDto, SubUser.class);
            subUserRepository.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSubUser(int subUserId) {
        try {
            subUserRepository.deleteById(subUserId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateSubUser(SubUserDto subDto) {
        try {
            SubUser subUser = mapper.map(subDto, SubUser.class);
            subUser.setUser(subUserRepository.findById(subDto.getSubUserId()).get().getUser());
            subUserRepository.save(subUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean subUserExists(String name, String email) {
        return subUserRepository.findByUserEmailAndName(email, name).isPresent();
    }

    public boolean subUserExists(int subUserId) {
        return subUserRepository.existsBySubUserId(subUserId);
    }
}