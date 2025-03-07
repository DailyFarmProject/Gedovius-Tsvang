package dailyfarm.product.entity.product;

import java.time.LocalDate;

import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.product.dto.ProductRequsetDto;
import dailyfarm.product.entity.AbstractProduct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends AbstractProduct {

	@Column(nullable = false)
	private double weight;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private LocalDate productionDate;

	@Column(nullable = false)
	private LocalDate expiryDate;


	public Product(String name, double price, double weight, String category, String imageUrl, LocalDate productionDate,
			LocalDate expiryDate, SellerAccount seller) {
		super(null, name, price, imageUrl, seller);
		this.weight = weight;
		this.category = category;
		this.productionDate = productionDate;
		this.expiryDate = expiryDate;

	}

	public static Product of(ProductRequsetDto dto, SellerAccount seller) {
		return new Product(dto.name(), dto.price(), dto.weight(), dto.category(), dto.imageUrl(), dto.productionDate(),
				dto.expiryDate(), seller);
	}
}
