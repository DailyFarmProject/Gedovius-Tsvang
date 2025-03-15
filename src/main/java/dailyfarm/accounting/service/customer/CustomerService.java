package dailyfarm.accounting.service.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.dto.customer.CustomerRequestDto;
import dailyfarm.accounting.dto.customer.CustomerResponseDto;
import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.exceptions.AccountActivationException;
import dailyfarm.accounting.exceptions.AccountRevokeException;
import dailyfarm.accounting.exceptions.PasswordNotValidException;
import dailyfarm.accounting.exceptions.RoleExistsException;
import dailyfarm.accounting.exceptions.RoleNotExistsException;
import dailyfarm.accounting.exceptions.UserExistsException;
import dailyfarm.accounting.exceptions.UserNotFoundException;
import dailyfarm.accounting.repository.customer.CustomerRepository;
import dailyfarm.accounting.repository.seller.SellerRepository;
import dailyfarm.accounting.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService implements ICustomerManagement {

	private final CustomerRepository customerRepo;
	private final SellerRepository sellerRepo;
	private final PasswordEncoder encoder;
	private final JwtUtils jwtUtils;
	private final AuthenticationManager authManager;

	@Value("${password_length:8}")
	private int passwordLength;

	@Value("${n_last_hash:3}")
	private int n_last_hash;

	@Override
	public TokenResponseDto login(LoginRequestDto dto) {
		log.debug("Attempting login for user: {}", dto.login());
		authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.login(), dto.password()));
		CustomerAccount customer = getCustomerAccount(dto.login());
		String token = jwtUtils.generateToken(customer.getLogin(), customer.getEmail(), customer.getRoles());
		log.info("Login successful, token generated for user: {}", dto.login());
		return new TokenResponseDto(token);
	}

	@Override
	public CustomerResponseDto registration(CustomerRequestDto user) {
		if (customerRepo.findByLogin(user.login()).isPresent()) {
			throw new UserExistsException(user.login());
		}
		if (!isPasswordValid(user.password())) {
			throw new PasswordNotValidException(user.password());
		}

		String hashedPassword = encoder.encode(user.password());
		CustomerAccount client = CustomerAccount.of(user);
		client.setHash(hashedPassword);

		log.info("Saving new customer: {}", client);
		client = customerRepo.save(client);
		log.info("Customer saved successfully: {}", client.getLogin());

		return CustomerResponseDto.build(client);
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	private CustomerAccount getCustomerAccount(String login) {
		return customerRepo.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
	}

	@Transactional
	@Override
	public CustomerResponseDto removeUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		customerRepo.deleteByLogin(login);
		return CustomerResponseDto.build(client);
	}

	@Override
	public CustomerResponseDto getUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return CustomerResponseDto.build(client);
	}

	@Transactional
	@Override
	public boolean updatePassword(String login, String oldPassword, String newPassword) {
		if (newPassword == null || !isPasswordValid(newPassword)) {
			throw new PasswordNotValidException("Invalid new password: " + newPassword);
		}
		CustomerAccount client = getCustomerAccount(login);

		log.info("Updating password for user: {}", login);
		if (!encoder.matches(oldPassword, client.getHash())) {
			log.warn("Old password does not match for user: {}", login);
			throw new PasswordNotValidException("Incorrect old password");
		}
		log.info("Old password verified successfully");
		if (isPasswordFromLast(newPassword, client.getLastHash())) {
			log.warn("New password was previously used for user: {}", login);
			throw new PasswordNotValidException("New password should not match the previous ones");
		}
		List<String> lastHash = client.getLastHash();
		if (lastHash.size() == n_last_hash) {
			lastHash.remove(0);
		}
		lastHash.add(client.getHash());
		client.setHash(encoder.encode(newPassword));
		client.setActivationDate(LocalDateTime.now());
		customerRepo.save(client);
		log.info("Password updated successfully for user: {}", login);
		return true;
	}

	private boolean isPasswordFromLast(String newPassword, List<String> lastHash) {
		return lastHash.stream().anyMatch(p -> encoder.matches(newPassword, p));
	}

	@Transactional
	@Override
	@PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public boolean updateUser(String login, CustomerRequestDto user) {

		CustomerAccount client = getCustomerAccount(login);

		if (user.password() != null) {
			log.warn("Password update attempted via updateUser for login: {}", login);
			throw new IllegalArgumentException("customer/password endpoint to update password");
		}
		if (user.email() != null)
			client.setEmail(user.email());
		if (user.firstName() != null)
			client.setFirstName(user.firstName());
		if (user.lastName() != null)
			client.setLastName(user.lastName());
		if (user.address() != null)
			client.setAddress(user.address());
		if (user.phone() != null)
			client.setPhone(user.phone());

		customerRepo.save(client);
		log.info("Customer updated successfully: {}", login);
		return true;
	}

	@Transactional
	@Override
	public boolean revokeAccount(String login) {
		CustomerAccount client = getCustomerAccount(login);
		if (client.isRevoked()) {
			throw new AccountRevokeException(login);
		}
		client.setRevoked(true);
		customerRepo.save(client);
		return true;
	}

	@Transactional
	@Override
	public boolean activateAccount(String login) {
		CustomerAccount client = getCustomerAccount(login);
		if (!client.isRevoked())
			throw new AccountActivationException(login);
		client.setRevoked(false);
		client.setActivationDate(LocalDateTime.now());
		customerRepo.save(client);
		return true;
	}

	@Override
	public RolesResponseDto getRoles(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : new RolesResponseDto(login, client.getRoles());
	}

	@Transactional
	@Override
	public boolean addRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);

		Set<String> roles = client.getRoles();
		if (roles.contains(role))
			throw new RoleExistsException(role);
		roles.add(role);
		customerRepo.save(client);
		return true;

	}

	@Transactional
	@Override
	public boolean removeRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);

		Set<String> roles = client.getRoles();
		if (!roles.contains(role))
			throw new RoleNotExistsException(role);
		roles.remove(role);
		customerRepo.save(client);
		return true;
	}

	@Override
	public String getPasswordHash(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : client.getHash();
	}

	@Override
	public LocalDateTime getActivationDate(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : client.getActivationDate();
	}
	
	public ResponseEntity<List<SellerAccount>> getAllSellers() {
		 List<SellerAccount> sellers = sellerRepo.findAll();
	        return ResponseEntity.ok(sellers);
	}	
}
