package dailyfarm.accounting.dto.customer;

import dailyfarm.accounting.entity.customer.CustomerAccount;

public record CustomerResponseDto(
		String login,  
		String email, 
		String firstName, 
		String lastName,
		String address, 
		String phone
		) {
	
public static CustomerResponseDto build(CustomerAccount customer) {
	return new CustomerResponseDto(customer.getLogin(), customer.getEmail(), customer.getFirstName(), customer.getLastName(), customer.getAddress(), customer.getPhone());
	
}
}
