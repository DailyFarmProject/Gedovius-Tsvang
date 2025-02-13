package dailyfarm.accounting.dto;

public record SupplierRequestDto(
		
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
