package dailyfarm.accounting.entity.seller;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import dailyfarm.accounting.dto.seller.SellerRequestDto;
import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.product.entity.product.Product;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sellers")
public class SellerAccount extends UserAccount {

    @Column(nullable = false, length = 50, unique = true)
    private String companyName;

    @Column(nullable = false, length = 255)
    private String companyAddress;

    @Column(nullable = false, length = 20, unique = true)
    private String taxId;

    @Column(nullable = false, length = 50)
    private String contactPerson;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sellers_roles", joinColumns = @JoinColumn(name = "seller_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime activationDate = LocalDateTime.now();

    @Column(columnDefinition = "geometry(Point,4326)", nullable = true)
    private Point location;

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
        this.location = null;
    }

   

    public void setCoordinates(double longitude, double latitude) {
        if (longitude >= -180 && longitude <= 180 && latitude >= -90 && latitude <= 90) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            point.setSRID(4326);
            this.location = point;
        } else {
            log.warn("Invalid coordinates: longitude = {}, latitude = {}", longitude, latitude);
            this.location = null;
        }
    }

    public static SellerAccount of(SellerRequestDto dto) {
        return new SellerAccount(dto.login(), null, dto.email(), dto.companyName(), dto.companyAddress(), dto.taxId(),
                dto.contactPerson(), dto.phone());
    }
}

