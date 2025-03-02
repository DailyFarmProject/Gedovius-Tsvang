package dailyfarm.accounting.dto.customer;


public record CustomerRequestDto(
		
		String login, 
		String password, 
		String email, 
		String firstName, 
		String lastName,
		String address, 
		String phone
		) {

}
