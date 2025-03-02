package dailyfarm.accounting.controllers;

import java.security.Principal;
import java.time.LocalDateTime;

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

import dailyfarm.accounting.dto.CustomerRequestDto;
import dailyfarm.accounting.dto.CustomerResponseDto;
import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.service.ICustomerManagement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

	private final ICustomerManagement service;

	@PostMapping("/register")
	public CustomerResponseDto registration(@Valid @RequestBody CustomerRequestDto customer) {
		return service.registration(customer);
	}

	@PostMapping("/auth/login")
	public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
		TokenResponseDto tokenResponse = service.login(dto);
        return ResponseEntity.ok(tokenResponse);
	}
	
	@DeleteMapping("/{login}")
	public CustomerResponseDto remove(@PathVariable String login) {
		return service.removeUser(login);
	}

	@GetMapping("/{login}")
	public CustomerResponseDto getUser(@PathVariable String login) {
		return service.getUser(login);
	}
	
	@PutMapping("/password")
	public ResponseEntity<String> updatePassword(
	    @RequestHeader("Old-Password") String oldPassword,
	    @RequestHeader("New-Password") String newPassword,
	    Principal principal
	) {
	    boolean updated = service.updatePassword(principal.getName(), oldPassword, newPassword);
	    
	    if (updated) {
	        return ResponseEntity.ok("Password updated successfully");
	    } else {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid old password or permission denied");
	    }
	}

	
	@PutMapping("/{login}")
	public ResponseEntity<String> updateUser(@PathVariable String login,@Valid @RequestBody CustomerRequestDto user) {
	    boolean updated = service.updateUser(login, user);
	    return updated 
	        ? ResponseEntity.ok("User updated successfully")
	        : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update user");
	}
	

	@PutMapping("/revoke/{login}")
	public ResponseEntity<String> revokeAccount(@PathVariable String login) {
		boolean revoked = service.revokeAccount(login);
        return revoked
            ? ResponseEntity.ok("Account revoked successfully")
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to revoke account");
    }
	
	@PutMapping("/activate/{login}")
    public ResponseEntity<String> activateAccount(@PathVariable String login) {
        boolean activated = service.activateAccount(login);
        return activated
            ? ResponseEntity.ok("Account activated successfully")
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to activate account");
    }

	@GetMapping("/roles/{login}")
	public RolesResponseDto getRoles(@PathVariable String login) {
		return service.getRoles(login);
	}

	@PutMapping("/{login}/role/{role}")
    public ResponseEntity<String> addRole(@PathVariable String login, @PathVariable String role) {
        boolean added = service.addRole(login, role);
        return added
            ? ResponseEntity.ok("Role added successfully")
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add role");
    }

	@DeleteMapping("/{login}/role/{role}")
    public ResponseEntity<String> removeRole(@PathVariable String login, @PathVariable String role) {
        boolean removed = service.removeRole(login, role);
        return removed
            ? ResponseEntity.ok("Role removed successfully")
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove role");
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

