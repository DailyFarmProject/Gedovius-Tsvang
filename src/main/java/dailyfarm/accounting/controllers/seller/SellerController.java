package dailyfarm.accounting.controllers.seller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.dto.seller.SellerRequestDto;
import dailyfarm.accounting.dto.seller.SellerResponseDto;
import dailyfarm.accounting.service.seller.SellerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/seller")
public class SellerController {

	@Autowired
	private SellerService service;

	@PostMapping("/register")
	public SellerResponseDto registration(@RequestBody SellerRequestDto supplier) {
		return service.registration(supplier);
	}

	@PostMapping("/auth/login")
	public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
		TokenResponseDto tokenResponse = service.login(dto);
		return ResponseEntity.ok(tokenResponse);
	}

	@DeleteMapping("/{login}")
	public SellerResponseDto remove(@PathVariable String login) {
		return service.removeUser(login);
	}

	@GetMapping("/{login}")
	public SellerResponseDto getUser(@PathVariable String login) {
		return service.getUser(login);
	}

	@PutMapping("/password")
	public ResponseEntity<String> updatePassword(@RequestHeader("Old-Password") String oldPassword,
			@RequestHeader("New-Password") String newPassword, Principal principal) {
		String login = principal.getName();

		boolean updated = service.updatePassword(login, oldPassword, newPassword);

		if (updated) {
			return ResponseEntity.ok("Password updated successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid old password or permission denied");
		}
	}

	@PutMapping("/{login}")
	public boolean updateUser(@PathVariable String login, @RequestBody SellerRequestDto user) {
		return service.updateUser(login, user);
	}

	@PutMapping("/revoke/{login}")
	public boolean revokeAccount(@PathVariable String login) {
		return service.revokeAccount(login);
	}

	@PutMapping("/activate/{login}")
	public boolean activateAccount(@PathVariable String login) {
		return service.activateAccount(login);
	}

	@GetMapping("/roles/{login}")
	public RolesResponseDto getRoles(@PathVariable String login) {
		return service.getRoles(login);
	}

	@PutMapping("/{login}/role/{role}")
	public boolean addRole(@PathVariable String login, @PathVariable String role) {
		return service.addRole(login, role);
	}

	@DeleteMapping("/{login}/role/{role}")
	public boolean removeRole(@PathVariable String login, @PathVariable String role) {
		return service.removeRole(login, role);
	}

	@GetMapping("/password/{login}")
	public String getPasswordHash(@PathVariable String login) {
		return service.getPasswordHash(login);
	}

	@GetMapping("/activation/{login}")
	public LocalDateTime getActivationDate(@PathVariable String login) {
		return service.getActivationDate(login);
	}


}
