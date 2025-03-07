package dailyfarm.product.dto;

import dailyfarm.product.entity.surprisebag.SurpriseBag;

public record SurpriseBagResponseDto(
        Long id,
        String name,
        double price,
        String description,
        int quantity,
        String imageUrl,
        String sellerName,
        String sellerAddress
) {
    public static SurpriseBagResponseDto build(SurpriseBag surpriseBag) {
        return new SurpriseBagResponseDto(
                surpriseBag.getId(),
                surpriseBag.getName(),
                surpriseBag.getPrice(),
                surpriseBag.getDescription(),
                surpriseBag.getQuantity(),
                surpriseBag.getImageUrl(), 
                surpriseBag.getSeller().getCompanyName(),
                surpriseBag.getSeller().getCompanyAddress()
        );
    }
}