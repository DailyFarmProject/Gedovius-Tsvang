package dailyfarm.accounting.dto;

import dailyfarm.accounting.entity.SupplierAccount;

public record SupplierResponseDto(
		String companyName, 
		String companyAddress,
		String email,
		String phone
		) {
public static SupplierResponseDto build(SupplierAccount supplier) {
	return  new SupplierResponseDto(supplier.getCompanyName(), supplier.getCompanyAddress(), supplier.getEmail(), supplier.getPhone());
	
}
}
