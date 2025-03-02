package dailyfarm.accounting.dto.seller;

public record SellerRequestDto(
		
		String login, 
		String password, 
		String email, 
		String companyName, 
		String companyAddress,
		String taxId, 
		String contactPerson,
		String phone
		) {

}
