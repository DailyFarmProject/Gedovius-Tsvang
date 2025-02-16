package dailyfarm.accounting.entity;

import dailyfarm.accounting.dto.CustomerRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class CustomerAccount extends UserAccount {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "customer_supplier",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    private Set<SupplierAccount> suppliers = new HashSet<>();
    
    @Override
    public Set<String> getRoles() {
        return roles;
    }

    public CustomerAccount(String login, String hash, String email, String firstName, String lastName, String address, String phone) {
        super(login, hash, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.roles.add("CUSTOMER");
    }

    public static CustomerAccount of(CustomerRequestDto dto) {
        return new CustomerAccount(
                dto.login(),
                dto.email(),
                dto.firstName(),
                dto.lastName(),
                dto.address(),
                dto.phone(),
                dto.email()
        );
    }
}
