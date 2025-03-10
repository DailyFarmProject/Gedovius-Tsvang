package dailyfarm.accounting.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.accounting.entity.admin.AdminAccount;
import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.repository.admin.AdminRepository;
import dailyfarm.accounting.repository.customer.CustomerRepository;
import dailyfarm.accounting.repository.seller.SellerRepository;


@Configuration
public class AuthenticationConfiguration implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private SellerRepository sellerRepo;

	@Autowired
	private AdminRepository adminRepo; 

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		Optional<AdminAccount> admin = adminRepo.findByLogin(login);
		if (admin.isPresent()) {
			return buildUserDetails(admin.get());
		}

		Optional<CustomerAccount> customer = customerRepo.findByLogin(login);
		if (customer.isPresent()) {
			return buildUserDetails(customer.get());
		}

		Optional<SellerAccount> supplier = sellerRepo.findByLogin(login);
		if (supplier.isPresent()) {
			return buildUserDetails(supplier.get());
		}

		throw new UsernameNotFoundException("User not found: " + login);
	}

	private UserDetails buildUserDetails(UserAccount user) {
	    return new User(user.getLogin(), user.getHash(),
	        AuthorityUtils.createAuthorityList(user.getRoles()
	            .stream().toArray(String[]::new)
	        )
	    );
	}
}
