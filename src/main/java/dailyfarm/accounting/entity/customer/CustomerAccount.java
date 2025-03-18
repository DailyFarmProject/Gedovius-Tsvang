package dailyfarm.accounting.entity.customer;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import dailyfarm.accounting.dto.customer.CustomerRequestDto;
import dailyfarm.accounting.entity.UserAccount;
import dailyfarm.order.entity.Order;

@Slf4j
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
    
    @Column(columnDefinition = "geometry(Point,4326)", nullable = true)
    private Point location;
    
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