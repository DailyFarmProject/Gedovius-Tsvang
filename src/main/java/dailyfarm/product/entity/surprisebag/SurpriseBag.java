package dailyfarm.product.entity.surprisebag;

import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.product.dto.SurpriseBagRequestDto;
import dailyfarm.product.entity.AbstractProduct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "surprise_bags")
public class SurpriseBag extends AbstractProduct {

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int quantity;

    public SurpriseBag(String name, double price, String description, int quantity, String imageUrl, SellerAccount seller) {
        super(null, name, price, imageUrl, seller);
        this.description = description;
        this.quantity = quantity;
    }
    public static SurpriseBag of(SurpriseBagRequestDto dto, SellerAccount seller) {
        return new SurpriseBag(
                dto.name(),
                dto.price(),
                dto.description(),
                dto.quantity(),
                dto.imageUrl(),
                seller
        );
    }
}
