package dailyfarm.product.repository;

import dailyfarm.product.entity.surprisebag.SurpriseBag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SurpriseBagRepository extends JpaRepository<SurpriseBag, Long> {
	
	List<SurpriseBag> findByNameContainingIgnoreCase(String name);
	
	List<SurpriseBag> findByPriceBetween(Double minPrice, Double maxPrice);

    List<SurpriseBag> findBySellerLogin(String sellerLogin);
}