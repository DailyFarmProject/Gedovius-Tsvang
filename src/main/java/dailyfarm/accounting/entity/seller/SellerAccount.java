package dailyfarm.accounting.entity.seller;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dailyfarm.accounting.dto.seller.SellerRequestDto;
import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.product.entity.product.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sellers")
public class SellerAccount extends UserAccount {

	@Column(nullable = false, length = 50)
	private String companyName;

	@Column(nullable = false, length = 255)
	private String companyAddress;

	@Column(nullable = false, length = 20)
	private String taxId;

	@Column(nullable = false, length = 50)
	private String contactPerson;

	@Column(nullable = false, length = 20)
	private String phone;
	
	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "sellers_roles", joinColumns = @JoinColumn(name = "seller_id"))
	@Column(name = "role")
	private Set<String> roles = new HashSet<>();

	
	   @Column(nullable = false)
	    private LocalDateTime activationDate = LocalDateTime.now();

	@Override
	public Set<String> getRoles() {
		return roles.stream().map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).collect(Collectors.toSet());
	}

	public SellerAccount(String login, String hash, String email, String companyName, String companyAddress,
			String taxId, String contactPerson, String phone) {
		super(login, hash, email);
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.taxId = taxId;
		this.contactPerson = contactPerson;
		this.phone = phone;
		this.roles.add("ROLE_SELLER");
		this.activationDate = LocalDateTime.now();
	}

	public static SellerAccount of(SellerRequestDto dto) {
		return new SellerAccount(dto.login(), null, dto.email(), dto.companyName(), dto.companyAddress(), dto.taxId(),
				dto.contactPerson(), dto.phone());
	}
}
