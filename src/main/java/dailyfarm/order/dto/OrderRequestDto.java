package dailyfarm.order.dto;

import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {
	@NotNull(message = "SurpriseBag ID must not be null")
    Long surpriseBagId;

}
