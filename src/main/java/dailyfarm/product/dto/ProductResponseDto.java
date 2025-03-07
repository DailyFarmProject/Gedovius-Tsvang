package dailyfarm.product.dto;

import java.time.LocalDate;

import dailyfarm.product.entity.product.Product;

public record ProductResponseDto(
		Long id,
		String name,
		double price,
		double weight,
		String category,
		String imageUrl,
		String sellerName,
		LocalDate productionDate,
		LocalDate expiryDate,
		String location
		) {
public static ProductResponseDto build(Product product) {
	return new ProductResponseDto(product.getId(), product.getName(),
			product.getPrice(), product.getWeight(), product.getCategory(),
			product.getImageUrl(), product.getSeller().getCompanyName(),
		 product.getProductionDate(), product.getExpiryDate(), product.getSeller().getCompanyAddress());
	
}
}
