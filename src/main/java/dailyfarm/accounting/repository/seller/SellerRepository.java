package dailyfarm.accounting.repository.seller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.accounting.entity.seller.SellerAccount;

public interface SellerRepository extends JpaRepository<SellerAccount, Long> {

	Optional<SellerAccount> findByLogin(String login);
	Optional<SellerAccount> findByCompanyName(String companyName);
	void deleteByLogin(String login);
	boolean existsByCompanyName(String companyName);

	
	



}
