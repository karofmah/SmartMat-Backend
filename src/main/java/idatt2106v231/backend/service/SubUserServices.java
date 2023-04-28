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

        List<SubUser> subUsers = subUserRepository.findAllByMasterUserEmail(email);
        List<SubUserDto> list = new ArrayList<>();
        subUsers.forEach(obj -> list.add(mapper.map(obj, SubUserDto.class)));
        return list;
    }

    public SubUserDto getSubUserByMasterAndName(String email, String name) {

        Optional<SubUser> subUser = subUserRepository.findByMasterUserEmailAndName(email, name);
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

    public boolean deleteSubUser(SubUserDto subUser) {
        try {
            subUserRepository.delete(subUserRepository.findByMasterUserEmailAndName(subUser.getMasterUserEmail(), subUser.getName()).get());
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
}
