package dailyfarm.accounting.entity.admin;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.Set;

import dailyfarm.accounting.entity.UserAccount;

@Getter
@NoArgsConstructor
@Entity
public class AdminAccount extends UserAccount {
    
    public AdminAccount(String login, String hash, String email) {
        super(login, hash, email);
    }
    
    @Override
    public Set<String> getRoles() {
        return Collections.singleton("ROLE_ADMIN");
    }
}

