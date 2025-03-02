package dailyfarm.accounting.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.accounting.entity.admin.AdminAccount;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminAccount, Long> {
    Optional<AdminAccount> findByLogin(String login);
}