package dailyfarm.accounting.dto;


import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RolesResponseDto {
	private String login;
	private Set<String> roles;
	
}
