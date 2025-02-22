package dailyfarm.accounting.entity;


import java.util.Collections;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class AdminAccount extends UserAccount {

    @Override
    public Set<String> getRoles() {
        return Collections.singleton("ROLE_ADMIN");
    }

    public AdminAccount(String login, String hash, String email) {
    	super(login, hash, email);
    }

    public AdminAccount() {
        super();
    }
}
