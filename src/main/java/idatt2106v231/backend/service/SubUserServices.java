package idatt2106v231.backend.service;

import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.repository.SubUserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class SubUserServices {

    private static final Logger logger = LoggerFactory.getLogger(SubUserServices.class);

    @Autowired
    private SubUserRepository subUserRepository;

    private final ModelMapper mapper = new ModelMapper();

    public Optional<SubUser> getSubUsersByMaster(String email) {
        return subUserRepository.findAllByMasterUserEmail(email);
    }
}
