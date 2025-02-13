package dailyfarm.accounting.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dailyfarm.accounting.dto.CustomerRequestDto;
import dailyfarm.accounting.dto.CustomerResponseDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.exceptions.AccountActivationException;
import dailyfarm.accounting.dto.exceptions.AccountRevokeException;
import dailyfarm.accounting.dto.exceptions.PasswordNotValidException;
import dailyfarm.accounting.dto.exceptions.RoleExistsException;
import dailyfarm.accounting.dto.exceptions.RoleNotExistsException;
import dailyfarm.accounting.dto.exceptions.UserExistsException;
import dailyfarm.accounting.dto.exceptions.UserNotFoundException;
import dailyfarm.accounting.entity.CustomerAccount;
import dailyfarm.accounting.repository.CustomerRepository;
import jakarta.transaction.Transactional;
@Service
public class CustomerService implements ICustomerManagement {

	@Autowired
	CustomerRepository repo;

	@Autowired
	PasswordEncoder encoder;

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
	public CustomerResponseDto removeUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		repo.deleteByLogin(login);

		return CustomerResponseDto.build(client);
	}

	@Override
	public CustomerResponseDto getUser(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return CustomerResponseDto.build(client);
	}

	@Transactional
	@Override
	public boolean updatePassword(String login, String newPassword) {
		if (newPassword == null || !isPasswordValid(newPassword)) {
			throw new PasswordNotValidException(newPassword);
		}
		CustomerAccount client = getCustomerAccount(login);
		if (encoder.matches(newPassword, client.getHash()))
			throw new PasswordNotValidException(newPassword);

		LinkedList<String> lastHash = client.getLastHash();
		if (isPasswordFromLast(newPassword, lastHash)) {
			throw new PasswordNotValidException(newPassword);
		}

		if (lastHash.size() == n_last_hash)
			lastHash.removeFirst();
		lastHash.add(client.getHash());
		client.setHash(encoder.encode(newPassword));
		client.setActivationDate(LocalDateTime.now());
		repo.save(client);
		return true;
	}

	private boolean isPasswordFromLast(String newPassword, LinkedList<String> lastHash) {
		return lastHash.stream().anyMatch(p -> encoder.matches(newPassword, p));

	}

	@Transactional
	@Override
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
	public RolesResponseDto getRoles(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : new RolesResponseDto(login, client.getRoles());
	}
	@Transactional
	@Override
	public boolean addRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);

		HashSet<String> roles = client.getRoles();
		if (roles.contains(role))
			throw new RoleExistsException(role);
		roles.add(role);
		repo.save(client);
		return true;
	
	}
	@Transactional
	@Override
	public boolean removeRole(String login, String role) {
		CustomerAccount client = getCustomerAccount(login);
		
		HashSet<String> roles = client.getRoles();
		if (!roles.contains(role))
			throw new RoleNotExistsException(role);
		roles.remove(role);
		repo.save(client);
		return true;	
	}

	@Override
	public String getPasswordHash(String login) {
		CustomerAccount client = getCustomerAccount(login);
		return client.isRevoked() ? null : client.getHash();
	}

	@Override
	public LocalDateTime getActivationDate(String login) {
		CustomerAccount farmer = getCustomerAccount(login);
		return farmer.isRevoked() ? null : farmer.getActivationDate();
	}


}
