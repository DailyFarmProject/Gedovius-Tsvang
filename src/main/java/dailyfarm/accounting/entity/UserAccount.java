package dailyfarm.accounting.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 255)
    private String hash;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private boolean revoked = false;

    @ElementCollection
    @CollectionTable(name = "user_last_hash", joinColumns = @JoinColumn(name = "user_account_id"))
    @Column(name = "last_hash")
    private List<String> lastHash = new ArrayList<>();
    
    public abstract Set<String> getRoles();

    public UserAccount(String login, String hash, String email) {
        this.login = login;
        this.hash = hash;
        this.email = email;
        
    }
}