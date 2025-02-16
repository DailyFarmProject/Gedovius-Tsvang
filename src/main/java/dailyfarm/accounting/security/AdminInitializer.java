package dailyfarm.accounting.security;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dailyfarm.accounting.entity.CustomerAccount;
import dailyfarm.accounting.entity.SupplierAccount;
import dailyfarm.accounting.repository.CustomerRepository;
import dailyfarm.accounting.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (isAdminPresent()) {
            logger.info("Admin '{}' already exists in both repositories.", ADMIN_LOGIN);
            return;
        }
        customerRepository.findByLogin(ADMIN_LOGIN).orElseGet(this::createAdminInCustomers);
        supplierRepository.findByLogin(ADMIN_LOGIN).orElseGet(this::createAdminInSuppliers);
    }

    private boolean isAdminPresent() {
        return customerRepository.findByLogin(ADMIN_LOGIN).isPresent() &&
               supplierRepository.findByLogin(ADMIN_LOGIN).isPresent();
    }

    private CustomerAccount createAdminInCustomers() {
        CustomerAccount admin = new CustomerAccount();
        admin.setLogin(ADMIN_LOGIN);
        admin.setHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setEmail(ADMIN_EMAIL);
        admin.setAddress("Default Address");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPhone("+123456789");

        admin.setRoles(new HashSet<>(ADMIN_ROLES));
        admin = customerRepository.save(admin);

        logger.info("Admin '{}' added to customers.", ADMIN_LOGIN);
        return admin;
    }

    private SupplierAccount createAdminInSuppliers() {
        SupplierAccount admin = new SupplierAccount();
        admin.setLogin(ADMIN_LOGIN);
        admin.setHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setEmail(ADMIN_EMAIL);
        admin.setCompanyName("admin");
        admin.setCompanyAddress("admin");
        admin.setTaxId("12345");
        admin.setContactPerson("admin");
        admin.setPhone("12345678");

        admin.setRoles(new HashSet<>(ADMIN_ROLES));
        admin = supplierRepository.save(admin);

        logger.info("Admin '{}' added to suppliers.", ADMIN_LOGIN);
        return admin;
    }
}
