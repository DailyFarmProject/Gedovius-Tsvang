package dailyfarm.accounting.security;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dailyfarm.accounting.entity.AdminAccount;
import dailyfarm.accounting.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@gmail.com";

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Optional<AdminAccount> existingAdmin = adminRepository.findByLogin(ADMIN_LOGIN);
        if (existingAdmin.isPresent()) {
            logger.info("Admin '{}' already exists.", ADMIN_LOGIN);
            return;
        }

        createAdmin();
    }

    private void createAdmin() {
        AdminAccount admin = new AdminAccount();
        admin.setLogin(ADMIN_LOGIN);
        admin.setHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setEmail(ADMIN_EMAIL);

        adminRepository.save(admin);
        logger.info("Admin '{}' has been created successfully.", ADMIN_LOGIN);
    }
}

