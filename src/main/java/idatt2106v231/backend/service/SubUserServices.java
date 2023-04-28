package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.repository.SubUserRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        TypeMap<SubUser, SubUserDto> propertyMapper = mapper.createTypeMap(SubUser.class, SubUserDto.class);
        propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getMasterUser().getEmail(), SubUserDto::setMasterUserEmail));

    }

    public List<SubUserDto> getSubUsersByMaster(String email) {
        return subUserRepository.findAllByMasterUserEmail(email).stream()
                .map(obj -> mapper.map(obj, SubUserDto.class)).toList();
    }

    public SubUserDto getSubUser(int subUserId) {
        Optional<SubUser> subUser = subUserRepository.findById(subUserId);
        return mapper.map(subUser.get(), SubUserDto.class);
    }

    public boolean saveSubUser(SubUserDto subUserDto) {
        try {
            SubUser subUser = mapper.map(subUserDto, SubUser.class);
            subUser.setMasterUser(userRepository.findByEmail(subUserDto.getMasterUserEmail()).get());
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

    public boolean pinCodeValid(SubUserDto subUserDto){
        try{
            SubUser subUser=subUserRepository.findDistinctByName(subUserDto.getName()).get();
            return subUser.getPinCode()==subUserDto.getPinCode();
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
