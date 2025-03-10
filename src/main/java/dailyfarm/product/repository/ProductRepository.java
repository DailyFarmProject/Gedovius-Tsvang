package dailyfarm.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.product.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
