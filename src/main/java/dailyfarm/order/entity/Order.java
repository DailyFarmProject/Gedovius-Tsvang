package dailyfarm.order.entity;

import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.product.entity.surprisebag.SurpriseBag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerAccount customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surprise_bag_id", nullable = false)
    private SurpriseBag surpriseBag;

    @Column(nullable = false)
    private String status;

    public Order() {
        this.status = "PENDING";
    }

    public static Order of(CustomerAccount customer, SurpriseBag surpriseBag) {
    	Order order = new Order();
        order.setCustomer(customer);
        order.setSurpriseBag(surpriseBag);
        return order;
    }
}
