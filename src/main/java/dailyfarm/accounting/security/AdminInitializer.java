package dailyfarm.accounting.security;

import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dailyfarm.accounting.entity.admin.AdminAccount;
import dailyfarm.accounting.repository.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    ;

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@gmail.com";

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
    }

    @Transactional
    private void createAdminIfNotExists() {
        Optional<AdminAccount> existingAdmin = adminRepository.findByLogin(ADMIN_LOGIN);
        if (existingAdmin.isPresent()) {
            log.info("Admin '{}' already exists.", ADMIN_LOGIN);
            return;
        }

        AdminAccount admin = new AdminAccount(ADMIN_LOGIN, passwordEncoder.encode(ADMIN_PASSWORD), ADMIN_EMAIL);
        adminRepository.save(admin);
        log.info("Admin '{}' has been created successfully.", ADMIN_LOGIN);
    }
}


