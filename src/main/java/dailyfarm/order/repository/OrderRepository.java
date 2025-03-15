package dailyfarm.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dailyfarm.order.entity.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {
		
	}

