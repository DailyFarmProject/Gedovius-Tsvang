package dailyfarm.accounting.entity.customer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dailyfarm.accounting.dto.customer.CustomerRequestDto;
import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.order.entity.Order;



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

    @Column(nullable = false, length = 20, unique = true)
    private String phone;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDateTime activationDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
    
    @Override
    public Set<String> getRoles() {
        return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toSet());
    }

    public CustomerAccount(String login, String hash, String email, String firstName, String lastName, String address, String phone) {
        super(login, hash, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.roles.add("ROLE_CUSTOMER");
        this.activationDate = LocalDateTime.now();
        
        
    }

    public static CustomerAccount of(CustomerRequestDto dto) {
        return new CustomerAccount(
                dto.login(),
                null,
                dto.email(),
                dto.firstName(),
                dto.lastName(),
                dto.address(),
                dto.phone()
        );
    }
}
