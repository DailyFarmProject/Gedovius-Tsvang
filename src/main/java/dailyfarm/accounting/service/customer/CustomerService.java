package dailyfarm.accounting.service.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.customer.CustomerRequestDto;
import dailyfarm.accounting.dto.customer.CustomerResponseDto;
import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.accounting.exceptions.AccountActivationException;
import dailyfarm.accounting.exceptions.AccountRevokeException;
import dailyfarm.accounting.exceptions.PasswordNotValidException;
import dailyfarm.accounting.exceptions.RoleExistsException;
import dailyfarm.accounting.exceptions.RoleNotExistsException;
import dailyfarm.accounting.exceptions.UserExistsException;
import dailyfarm.accounting.exceptions.UserNotFoundException;
import dailyfarm.accounting.repository.customer.CustomerRepository;
import dailyfarm.accounting.security.JwtUtils;
import jakarta.transaction.Transactional;

@Service
public class CustomerService implements ICustomerManagement {

	private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	CustomerRepository repo;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;

	@Value("${password_length:8}")
	private int passwordLength;

	@Value("${n_last_hash:3}")
	private int n_last_hash;

	@Override
	public CustomerResponseDto registration(CustomerRequestDto user) {

		if (repo.findByLogin(user.login()).isPresent()) {
			throw new UserExistsException(user.login());
		}
		if (!isPasswordValid(user.password()))
			throw new PasswordNotValidException(user.password());

		String hashedPassword = encoder.encode(user.password());

		CustomerAccount client = CustomerAccount.of(user);
		client.setHash(hashedPassword);

		log.info("Saving new customer: {}", client);
		client = repo.save(client);
		log.info("Customer saved successfully: {}", client.getLogin());

		return CustomerResponseDto.build(client);
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	private CustomerAccount getCustomerAccount(String login) {
		return repo.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
	}

	@Transactional
	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public CustomerResponseDto removeUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		repo.deleteByLogin(login);

		return CustomerResponseDto.build(client);
	}

	@Override
    @PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public CustomerResponseDto getUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return CustomerResponseDto.build(client);
	}

	@Transactional
	@Override
    @PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public boolean updatePassword(String login, String oldPassword, String newPassword) {
	    if (newPassword == null || !isPasswordValid(newPassword)) {
	        throw new PasswordNotValidException("Invalid new password: " + newPassword);
	    }
	    CustomerAccount client = getCustomerAccount(login);
	   
	    log.info("Updating password for user: {}", login);
	    log.info("Stored hash: {}", client.getHash());
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
	    repo.save(client);
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

		if (user.email() != null)
			client.setEmail(user.email());
		if (user.firstName() != null)
			client.setFirstName(login);
		if (user.lastName() != null)
			client.setLastHash(null);
		if (user.address() != null)
			client.setAddress(login);
		if (user.phone() != null)
			client.setPhone(user.phone());
		if (user.password() != null) {
			String hashedPassword = encoder.encode(user.password());
			client.setHash(hashedPassword);
		}

		repo.save(client);
		return true;
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean revokeAccount(String login) {
		CustomerAccount client = getCustomerAccount(login);
		if (client.isRevoked()) {
			throw new AccountRevokeException(login);
		}
		client.setRevoked(true);
		repo.save(client);
		return true;
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean activateAccount(String login) {
		CustomerAccount client = getCustomerAccount(login);
		if (!client.isRevoked())
			throw new AccountActivationException(login);
		client.setRevoked(false);
		client.setActivationDate(LocalDateTime.now());
		repo.save(client);
		return true;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public RolesResponseDto getRoles(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : new RolesResponseDto(login, client.getRoles());
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean addRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);

		Set<String> roles = client.getRoles();
		if (roles.contains(role))
			throw new RoleExistsException(role);
		roles.add(role);
		repo.save(client);
		return true;

	}

	@Transactional
	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public boolean removeRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);

		Set<String> roles = client.getRoles();
		if (!roles.contains(role))
			throw new RoleNotExistsException(role);
		roles.remove(role);
		repo.save(client);
		return true;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public String getPasswordHash(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : client.getHash();
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public LocalDateTime getActivationDate(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : client.getActivationDate();
	}
	
	}

