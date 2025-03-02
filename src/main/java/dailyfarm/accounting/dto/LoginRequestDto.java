package dailyfarm.accounting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
		@NotNull(message = "Login cannot be null")
		String login,
		@NotNull(message = "Password cannot be null")
		@Size(min = 8)
		String password
		
		) {}
