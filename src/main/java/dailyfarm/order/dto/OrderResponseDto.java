package dailyfarm.order.dto;

import dailyfarm.order.entity.Order;
import dailyfarm.product.entity.surprisebag.SurpriseBag;

public record OrderResponseDto(
		    Long id,
		    Long customerId,
		    Long surpriseBagId,
		    String surpriseBagName,
		    double price,
		    String status
		) {
		    public static OrderResponseDto build(Order order) {
		        SurpriseBag bag = order.getSurpriseBag();
		        return new OrderResponseDto(
		            order.getId(),
		            order.getCustomer().getId(),
		            bag.getId(),
		            bag.getName(),
		            bag.getPrice(),
		            order.getStatus()
		        );
		    }
		}
	

