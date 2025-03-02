package dailyfarm.accounting.repository.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.accounting.entity.customer.CustomerAccount;

public interface CustomerRepository extends JpaRepository<CustomerAccount, Long> {

	
Optional<CustomerAccount> findByLogin(String login);
	
	void deleteByLogin(String login);
	
}
