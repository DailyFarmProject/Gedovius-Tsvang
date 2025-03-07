package dailyfarm.product.entity;

import dailyfarm.accounting.entity.seller.SellerAccount;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column
    private String imageUrl; 

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerAccount seller;

    public String getSellerName() {
        return seller.getCompanyName();
    }

    public String getSellerAddress() {
        return seller.getCompanyAddress();
    }
}