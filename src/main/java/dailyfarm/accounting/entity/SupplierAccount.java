package dailyfarm.accounting.entity;

import dailyfarm.accounting.dto.SupplierRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "suppliers")
    private Set<CustomerAccount> customers = new HashSet<>();

    public SupplierAccount(String login, String hash, String email, String companyName, String companyAddress, String taxId, String contactPerson, String phone) {
        super(login, hash, email, "SUPPLIER");
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.taxId = taxId;
        this.contactPerson = contactPerson;
        this.phone = phone;
    }

    public static SupplierAccount of(SupplierRequestDto dto) {
        return new SupplierAccount(
                dto.login(),
                dto.email(),
                dto.companyName(),
                dto.companyAddress(),
                dto.taxId(),
                dto.contactPerson(),
                dto.phone(),
                dto.email()
        );
    }
}
