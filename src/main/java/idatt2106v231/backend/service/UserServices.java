package idatt2106v231.backend.service;

import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {

    private static final Logger _logger =
            LoggerFactory.getLogger(UserServices.class);
    private UserRepository userRepository;

    /**
     * Sets the user repository to use for database access.
     *
     * @param userRepository the user repository to use.
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * This method retrieves a user with a specified email address from the database.
     *
     * @param email the email address of the user to retrieve.
     * @return an Optional object containing the user with the specified email address, or an empty Optional object if the user does not exist in the database.
     */
    public Optional<User> getUser(String email) {
        Optional<User> userOptional = userRepository.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .or(Optional::empty);

        if (userOptional.isPresent()) {
            _logger.info("Successfully retrieved user with email: " + email);
        } else {
            _logger.info("Could not find user with email: " + email);
        }

        return userOptional;
    }
}
