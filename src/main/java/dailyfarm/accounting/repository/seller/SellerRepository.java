package dailyfarm.accounting.repository.seller;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dailyfarm.accounting.dto.seller.SellerWithDistanceDto;
import dailyfarm.accounting.entity.seller.SellerAccount;

public interface SellerRepository extends JpaRepository<SellerAccount, Long> {

	Optional<SellerAccount> findByLogin(String login);
	Optional<SellerAccount> findByCompanyName(String companyName);
	void deleteByLogin(String login);
	boolean existsByCompanyName(String companyName);

	@Query(value = """
		    SELECT DISTINCT s.company_name, s.company_address, u.email, s.phone,
		           ST_Distance(s.location::geography, ST_MakePoint(:longitude, :latitude)::geography) AS distance
		    FROM sellers s
		    JOIN user_account u ON s.id = u.id
		    WHERE ST_DWithin(s.location::geography, ST_MakePoint(:longitude, :latitude)::geography, :radius * 1000)
		    ORDER BY distance ASC
		""", nativeQuery = true)
		List<SellerWithDistanceDto> findNearestSellers(
		    @Param("longitude") double longitude,
		    @Param("latitude") double latitude,
		    @Param("radius") double radius
		);







	
}
