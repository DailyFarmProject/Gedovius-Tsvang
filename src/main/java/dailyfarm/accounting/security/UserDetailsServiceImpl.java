package dailyfarm.accounting.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dailyfarm.accounting.entity.admin.AdminAccount;
import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.repository.admin.AdminRepository;
import dailyfarm.accounting.repository.customer.CustomerRepository;
import dailyfarm.accounting.repository.seller.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	private final CustomerRepository customerRepo;
	private final SellerRepository supplierRepo;
	private final AdminRepository adminRepo; 

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		log.debug("Loading user by login: {}", login);
		
		Optional<AdminAccount> admin = adminRepo.findByLogin(login);
		if (admin.isPresent()) {
			log.debug("Admin found: {}", login);
			return UserDetailsImpl.build(admin.get());
			}

		Optional<CustomerAccount> customer = customerRepo.findByLogin(login);
		if (customer.isPresent()) {
			log.debug("Customer found: {}", login);
			return UserDetailsImpl.build(customer.get());
		}

		Optional<SellerAccount> supplier = supplierRepo.findByLogin(login);
		if (supplier.isPresent()) {
			log.debug("Supplier found: {}", login);
			return UserDetailsImpl.build(supplier.get());
		}
		log.warn("User not found: {}", login);
		throw new UsernameNotFoundException("User not found: " + login);
	
	}
}
