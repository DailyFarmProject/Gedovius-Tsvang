package dailyfarm.accounting.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime activationDate = LocalDateTime.now();

    @Column(nullable = false)
    private boolean revoked = false;

    @ElementCollection
    @CollectionTable(name = "user_last_hash", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "last_hash")
    private LinkedList<String> lastHash = new LinkedList<>();

    public UserAccount(String login, String hash, String email, String role) {
        this.login = login;
        this.hash = hash;
        this.email = email;
        this.roles.add(role);
        this.activationDate = LocalDateTime.now();
    }
}