package dailyfarm.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SurpriseBagRequestDto(
		@NotBlank(message = "Name must not be blank")
		String name,
		@Positive(message = "Price must be greater than 0")
        double price,
        @NotBlank(message = "Description must not be blank")
        String description,
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,
        @NotBlank(message = "Image URL must not be blank")
        String imageUrl
){
}
