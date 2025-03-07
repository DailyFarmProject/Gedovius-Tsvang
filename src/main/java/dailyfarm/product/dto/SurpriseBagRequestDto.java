package dailyfarm.product.dto;

public record SurpriseBagRequestDto(
		String name,
        double price,
        String description,
        int quantity,
        String imageUrl
){
}
