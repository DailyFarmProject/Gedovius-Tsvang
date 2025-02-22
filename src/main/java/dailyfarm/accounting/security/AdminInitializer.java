package dailyfarm.accounting.security;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dailyfarm.accounting.entity.AdminAccount;
import dailyfarm.accounting.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

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
            logger.info("Admin '{}' already exists.", ADMIN_LOGIN);
            return;
        }

        AdminAccount admin = new AdminAccount(ADMIN_LOGIN, passwordEncoder.encode(ADMIN_PASSWORD), ADMIN_EMAIL);
        adminRepository.save(admin);
        logger.info("Admin '{}' has been created successfully.", ADMIN_LOGIN);
    }
}


