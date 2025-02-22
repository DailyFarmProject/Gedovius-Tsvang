package dailyfarm.accounting.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dailyfarm.accounting.entity.CustomerAccount;
import dailyfarm.accounting.entity.SupplierAccount;
import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.accounting.repository.CustomerRepository;
import dailyfarm.accounting.repository.SupplierRepository;

@Configuration
public class AuthenticationConfiguration implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private SupplierRepository supplierRepo;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		Optional<CustomerAccount> customer = customerRepo.findByLogin(login);
		if (customer.isPresent()) {
			return buildUserDetails(customer.get());
		}
		Optional<SupplierAccount> supplier = supplierRepo.findByLogin(login);
		if (supplier.isPresent()) {
			return buildUserDetails(supplier.get());
		}
		throw new UsernameNotFoundException("User not found: " + login);
	}
	
	private UserDetails buildUserDetails(UserAccount user) {
	    String password = user.getHash();
	   
	    return new User(user.getLogin(), password, AuthorityUtils.createAuthorityList(user.getRoles()));
	}
}
