package idatt2106v231.backend.repository;

import idatt2106v231.backend.model.SubUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubUserRepository extends JpaRepository<SubUser, String> {

    Optional<SubUser> findDistinctBySubUserId(int subUserId);

    Optional<SubUser> findAllByMasterUserEmail(String email);
}
