package by.pavvel.repository;

import by.pavvel.model.reg.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser,Long> {
    Optional<ApplicationUser> getUserByEmail(String email);

    @Transactional
    @Modifying
    @Query("update ApplicationUser au set au.enabled = true where au.email =?1")
    void enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("update ApplicationUser au set au.password =?1 where au.email =?2")
    void changePassword(String password, String username);
}
