package dailyfarm.accounting.dto;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RolesResponseDto {
	private String login;
	private HashSet<String> roles;
	
}
