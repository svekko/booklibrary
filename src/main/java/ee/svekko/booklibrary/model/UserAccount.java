package ee.svekko.booklibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    @SequenceGenerator(name = "user_account_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_account_id_seq", strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    public static UserAccount fromSecurityContext() {
        UserSession userSession = UserSession.fromSecurityContext();
        return UserAccount.builder().id(userSession.id()).build();
    }
}
