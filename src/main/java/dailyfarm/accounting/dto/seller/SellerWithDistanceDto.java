package dailyfarm.accounting.dto.seller;


public record SellerWithDistanceDto(
        String companyName,
        String companyAddress,
        String email,
        String phone,
        double distance
) {
}