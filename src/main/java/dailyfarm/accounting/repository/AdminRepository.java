package dailyfarm.accounting.repository;

import dailyfarm.accounting.entity.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminAccount, Long> {
    Optional<AdminAccount> findByLogin(String login);
}