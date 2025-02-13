package dailyfarm.accounting.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dailyfarm.accounting.dto.CustomerRequestDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class CustomerAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(value = AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String login;

	@Column(nullable = false, length = 255)
	private String hash;

	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = false, length = 255)
	private String address;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, length = 50)
	private String email;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"))
	@Column(name = "role")
	@Builder.Default
	private HashSet<String> roles = new HashSet<>();

	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime activationDate = LocalDateTime.now();

	@Column(nullable = false)
	@Builder.Default
	private boolean revoked = false;

	@ElementCollection
	@Builder.Default
	@CollectionTable(name = "customer_last_hash", joinColumns = @JoinColumn(name = "customer_id"))
	@Column(name = "last_hash")
	private LinkedList<String> lastHash = new LinkedList<>();
	
	@ManyToMany
	@Builder.Default
	@JoinTable(name = "customer_supplier", joinColumns = @JoinColumn(name="customer_id"),
	inverseJoinColumns = @JoinColumn(name="supplier_id"))
	private Set<SupplierAccount> suppliers = new HashSet<>();

	public CustomerAccount(String login, String hash, String firstName, String lastName, String address, String phone,
			String email) {
		super();
		this.login = login;
		this.hash = hash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.email = email;
		roles.add("CUSTOMER");
		activationDate = LocalDateTime.now();
	}
	
	public static CustomerAccount of(CustomerRequestDto dto) {
		return CustomerAccount.builder()
						.login(dto.login())
						.email(dto.email())
						.firstName(dto.firstName())
						.lastName(dto.lastName())
						.address(dto.address())
						.phone(dto.phone())
						.build();
	}

}
