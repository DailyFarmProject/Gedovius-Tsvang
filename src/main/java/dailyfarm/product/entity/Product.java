package dailyfarm.product.entity;

import java.time.LocalDate;

import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.product.dto.ProductRequsetDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private double weight;

	@Column(nullable = false)
	private String category;
	
	@Column
	private String imageUrl;
	
	@Column(nullable = false)
	private String farmerName;
	
	@Column(nullable = false)
	private String farmName;
	
	@Column(nullable = false)
	private LocalDate productionDate;

	@Column(nullable = false)
	private LocalDate expiryDate;

	@Column(nullable = false)
	private String location;
	
	@ManyToOne
	@JoinColumn(name = "seller_id")
	private SellerAccount seller;

	public Product(String name, double price, double weight, String category, String imageUrl, String farmerName,
			String farmName, LocalDate productionDate, LocalDate expiryDate, String location) {

		this.name = name;
		this.price = price;
		this.weight = weight;
		this.category = category;
		this.imageUrl = imageUrl;
		this.farmerName = farmerName;
		this.farmName = farmName;
		this.productionDate = productionDate;
		this.expiryDate = expiryDate;
		this.location = location;
	}
		
public static Product of(ProductRequsetDto dto) {
	return new Product(
			dto.name(),
            dto.price(),
            dto.weight(),
            dto.category(),
            dto.imageUrl(),
            dto.farmerName(),
            dto.farmName(),
            dto.productionDate(),
            dto.expiryDate(),
            dto.location());
}
}
