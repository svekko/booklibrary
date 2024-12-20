package ee.smit.booklibrary.repository;

import ee.smit.booklibrary.model.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM user_account
            WHERE email = :email
            """
    )
    UserAccount findByEmail(String email);
}
