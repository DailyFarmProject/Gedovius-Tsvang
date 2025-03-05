package dailyfarm.product.dto;

import java.time.LocalDate;

public record ProductRequsetDto(
		String name,
		double price,
		double weight,
		String category,
		String imageUrl,
		String farmerName,
		String farmName,
		LocalDate productionDate,
		LocalDate expiryDate,
		String location
		) {

}
