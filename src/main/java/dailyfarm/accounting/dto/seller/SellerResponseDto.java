package dailyfarm.accounting.dto.seller;

import dailyfarm.accounting.entity.seller.SellerAccount;

public record SellerResponseDto(
		String companyName, 
		String companyAddress,
		String email,
		String phone
		) {
public static SellerResponseDto build(SellerAccount supplier) {
	return  new SellerResponseDto(supplier.getCompanyName(), supplier.getCompanyAddress(), supplier.getEmail(), supplier.getPhone());
	
}
}
