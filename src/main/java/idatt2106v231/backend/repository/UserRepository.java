package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

    User findDistinctByEmail(String email);
}
