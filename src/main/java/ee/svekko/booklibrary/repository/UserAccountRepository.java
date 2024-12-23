package ee.svekko.booklibrary.repository;

import ee.svekko.booklibrary.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM user_account
            WHERE email = :email
            """
    )
    Optional<UserAccount> findByEmail(String email);
}
