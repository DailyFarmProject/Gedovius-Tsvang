package dailyfarm.product.repository;

import dailyfarm.product.entity.surprisebag.SurpriseBag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurpriseBagRepository extends JpaRepository<SurpriseBag, Long> {
}