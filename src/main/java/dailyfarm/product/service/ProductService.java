package dailyfarm.product.service;

import org.springframework.stereotype.Service;

import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.product.dto.ProductRequsetDto;
import dailyfarm.product.dto.ProductResponseDto;
import dailyfarm.product.entity.product.Product;
import dailyfarm.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final ProductRepository repo;

	@Transactional
	public ProductResponseDto addProduct(ProductRequsetDto request, SellerAccount seller) {
		log.info("Adding Product by seller: {}", seller.getLogin());

		if (request.price() <= 0) {
			throw new IllegalArgumentException("Price must be greater than 0");
		}
		if (request.weight() <= 0) {
			throw new IllegalArgumentException("Weight cannot be negative");
		}

		Product product = Product.of(request, seller);

		Product saveProduct = repo.save(product);

		log.info("Product adde successfully: ID={}", saveProduct.getId());

		return ProductResponseDto.build(saveProduct);

	}

	@Transactional
	public void deletProduct(Long productId, SellerAccount seller) {
		log.info("Fetching product ID={}", productId);
		Product product = repo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product with ID: " + productId + " not found"));
		if (!product.getSeller().getLogin().equals(seller.getLogin())) {
			throw new IllegalStateException("Only the seller who created the product can delete it");
		}
		repo.delete(product);
		log.info("Product deleted succefully: ID={}", productId);

	}

	@Transactional
	public ProductResponseDto getProduct(Long productId) {
		log.info("Fetching product ID={}", productId);
		Product product = repo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID={}" + productId));
		return ProductResponseDto.build(product);
	}

	@Transactional
	public ProductResponseDto updateProduct(Long productId, ProductRequsetDto request, SellerAccount seller) {
		log.info("Updating product ID={} by seller", productId, seller.getLogin());
		Product product = repo.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product with ID: " + productId + " not found"));
		if (!product.getSeller().getLogin().equals(seller.getLogin())) {
			throw new IllegalStateException("Only the seller who created the product can delete it");
		}
		if (request.name() != null)
			product.setName(request.name());
		
		if (request.price() > 0)
			product.setPrice(request.price());
		
		else if (request.price() <= 0)
			throw new IllegalArgumentException("Price must be greater than 0");
		
		if (request.weight() > 0)
			product.setWeight(request.weight());
		
		else if (request.weight() <= 0)
			throw new IllegalArgumentException("Weight must be greater than 0");
		
		if (request.category() != null)
			product.setCategory(request.category());
		
		if (request.imageUrl() != null)
			product.setImageUrl(request.imageUrl());
		
		if (request.productionDate() != null)
			product.setProductionDate(request.productionDate());
		
		if (request.expiryDate() != null)
			product.setExpiryDate(request.expiryDate());

		Product updateProduct = repo.save(product);
		log.info("Product updated successfully: ID={}", updateProduct.getId());
		return ProductResponseDto.build(updateProduct);

	}

}
