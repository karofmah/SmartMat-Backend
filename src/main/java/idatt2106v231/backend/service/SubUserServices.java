package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserCreationDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.dto.subuser.SubUserValidationDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.repository.SubUserRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubUserServices {

    @Autowired
    private SubUserRepository subUserRepository;

    @Autowired
    private UserRepository userRepository;

    private final ModelMapper mapper = new ModelMapper();

    public SubUserServices() {
        TypeMap<SubUser, SubUserCreationDto> propertyMapper = mapper.createTypeMap(SubUser.class, SubUserCreationDto.class);
        propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getMasterUser().getEmail(), SubUserCreationDto::setMasterUserEmail));

    }

    public List<SubUserDto> getSubUsersByMaster(String email) { //sjekk
        return subUserRepository.findAllByMasterUserEmail(email).stream()
                .map(obj -> mapper.map(obj, SubUserDto.class)).toList();
    }

    public SubUserDto getSubUser(int subUserId) { //sjekk
        Optional<SubUser> subUser = subUserRepository.findById(subUserId);
        return mapper.map(subUser.get(), SubUserDto.class);
    }

    public boolean saveSubUser(SubUserCreationDto subUserCreationDto) {
        try {
            SubUser subUser = mapper.map(subUserCreationDto, SubUser.class);
            subUser.setMasterUser(userRepository.findByEmail(subUserCreationDto.getMasterUserEmail()).get());
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

    public boolean pinCodeValid(SubUserValidationDto subDto){
        try{
            SubUser subUser = subUserRepository.findById(subDto.getSubUserId()).get();
            return subUser.getPinCode() == subDto.getPinCode();
        }catch (Exception e){
            return false;
        }
    }

    public boolean subUserExists(String name, String email) {
        return subUserRepository.findByMasterUserEmailAndName(email, name).isPresent();
    }

    public boolean subUserExists(int subUserId) {
        return subUserRepository.existsBySubUserId(subUserId);
    }
}
