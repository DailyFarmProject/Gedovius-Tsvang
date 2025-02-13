package dailyfarm.accounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.accounting.entity.SupplierAccount;

public interface SupplierRepository extends JpaRepository<SupplierAccount, Long> {

	Optional<SupplierAccount> findByLogin(String login);
	
	void deleteByLogin(String login);

	
	



}
