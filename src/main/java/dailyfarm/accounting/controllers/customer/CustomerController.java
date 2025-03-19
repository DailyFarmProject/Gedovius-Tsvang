package dailyfarm.accounting.controllers.customer;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.dto.customer.CustomerRequestDto;
import dailyfarm.accounting.dto.customer.CustomerResponseDto;
import dailyfarm.accounting.dto.seller.SellerWithDistanceDto;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.service.customer.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService service;

	@PostMapping("/register")
	public CustomerResponseDto registration(@RequestBody CustomerRequestDto customer) {
		return service.registration(customer);
	}

	@PostMapping("/auth/login")
	public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
		TokenResponseDto tokenResponse = service.login(dto);
		return ResponseEntity.ok(tokenResponse);
	}

	@DeleteMapping("/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public CustomerResponseDto remove(@PathVariable String login) {
		return service.removeUser(login);
	}

	@GetMapping("/{login}")
	@PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public CustomerResponseDto getUser(@PathVariable String login) {
		return service.getUser(login);
	}

	@PutMapping("/password")
	public ResponseEntity<String> updatePassword(@RequestHeader("Old-Password") String oldPassword,
			@RequestHeader("New-Password") String newPassword, Principal principal) {
		boolean updated = service.updatePassword(principal.getName(), oldPassword, newPassword);

		if (updated) {
			return ResponseEntity.ok("Password updated successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid old password or permission denied");
		}
	}

	@PutMapping("/{login}")
	@PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public boolean updateUser(@PathVariable String login, @RequestBody CustomerRequestDto user) {
		return service.updateUser(login, user);
	}

	@PutMapping("/revoke/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean revokeAccount(@PathVariable String login) {
		return service.revokeAccount(login);
	}

	@PutMapping("/activate/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean activateAccount(@PathVariable String login) {
		return service.activateAccount(login);
	}

	@GetMapping("/roles/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public RolesResponseDto getRoles(@PathVariable String login) {
		return service.getRoles(login);
	}

	@PutMapping("/{login}/role/{role}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean addRole(@PathVariable String login, @PathVariable String role) {
		return service.addRole(login, role);
	}

	@DeleteMapping("/{login}/role/{role}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean removeRole(@PathVariable String login, @PathVariable String role) {
		return service.removeRole(login, role);
	}

	@GetMapping("/password/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public String getPasswordHash(@PathVariable String login) {
		return service.getPasswordHash(login);
	}

	@GetMapping("/activation/{login}")
	@PreAuthorize("hasRole('ADMIN')")
	public LocalDateTime getActivationDate(@PathVariable String login) {
		return service.getActivationDate(login);
	}

	@GetMapping("/sellers")
	public ResponseEntity<List<SellerAccount>> getAllSellers() {
		return service.getAllSellers();
	}

	@GetMapping("/nearest-sellers")
	@PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public List<SellerWithDistanceDto> getNearestSellers(@RequestParam double radius, @RequestParam String login) {
		return service.getNearestSellers(login, radius);
	}

}