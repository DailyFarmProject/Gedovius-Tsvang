package dailyfarm.accounting.entity;

import dailyfarm.accounting.dto.SupplierRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suppliers")
public class SupplierAccount extends UserAccount {

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

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "supplier_roles", joinColumns = @JoinColumn(name = "supplier_id"))
	@Column(name = "role")
	private Set<String> roles = new HashSet<>();

	@ManyToMany(mappedBy = "suppliers")
	private Set<CustomerAccount> customers = new HashSet<>();

	   @Column(nullable = false)
	    private LocalDateTime activationDate = LocalDateTime.now();

	@Override
	public Set<String> getRoles() {
		return roles.stream().map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role).collect(Collectors.toSet());
	}

	public SupplierAccount(String login, String hash, String email, String companyName, String companyAddress,
			String taxId, String contactPerson, String phone) {
		super(login, hash, email);
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.taxId = taxId;
		this.contactPerson = contactPerson;
		this.phone = phone;
		this.roles.add("ROLE_SUPPLIER");
		this.activationDate = LocalDateTime.now();
	}

	public static SupplierAccount of(SupplierRequestDto dto) {
		return new SupplierAccount(dto.login(), null, dto.email(), dto.companyName(), dto.companyAddress(), dto.taxId(),
				dto.contactPerson(), dto.phone());
	}
}
