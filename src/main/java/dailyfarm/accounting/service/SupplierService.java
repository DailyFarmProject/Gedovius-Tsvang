package dailyfarm.accounting.service;

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
import dailyfarm.accounting.dto.SupplierRequestDto;
import dailyfarm.accounting.dto.SupplierResponseDto;
import dailyfarm.accounting.dto.exceptions.AccountActivationException;
import dailyfarm.accounting.dto.exceptions.AccountRevokeException;
import dailyfarm.accounting.dto.exceptions.PasswordNotValidException;
import dailyfarm.accounting.dto.exceptions.RoleExistsException;
import dailyfarm.accounting.dto.exceptions.RoleNotExistsException;
import dailyfarm.accounting.dto.exceptions.UserExistsException;
import dailyfarm.accounting.dto.exceptions.UserNotFoundException;
import dailyfarm.accounting.entity.SupplierAccount;
import dailyfarm.accounting.repository.SupplierRepository;
import jakarta.transaction.Transactional;
@Service
public class SupplierService implements ISupplierManagement {

	private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

	
	@Autowired
	SupplierRepository repo;

	@Autowired
	PasswordEncoder encoder;

	@Value("${password_length:8}")
	private int passwordLength;

	@Value("${n_last_hash:3}")
	private int n_last_hash;

	@Override
	public SupplierResponseDto registration(SupplierRequestDto user) {

		if (repo.findByLogin(user.login()).isPresent()) {
			throw new UserExistsException(user.login());
		}
		if (!isPasswordValid(user.password()))
			throw new PasswordNotValidException(user.password());

		String hashedPassword = encoder.encode(user.password());

		SupplierAccount farmer = SupplierAccount.of(user);
		farmer.setHash(hashedPassword);
		
		log.info("Saving new supplier: {}", farmer);
		farmer = repo.save(farmer);
		log.info("Supplier saved successfully: {}", farmer.getLogin());

		return SupplierResponseDto.build(farmer);
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public SupplierResponseDto removeUser(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		repo.deleteByLogin(login);

		return SupplierResponseDto.build(farmer);
	}
	
	private SupplierAccount getSupplierAccount(String login) {
		return repo.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
	}

	@Override
    @PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public SupplierResponseDto getUser(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		return SupplierResponseDto.build(farmer);
	}

	@Transactional
	@Override
    @PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public boolean updatePassword(String login, String oldPassword, String newPassword) {
	    log.info("Attempting password update for supplier: {}", login);
	    if (newPassword == null || !isPasswordValid(newPassword)) {
	        log.warn("Invalid new password provided for user: {}", login);
	        throw new PasswordNotValidException("Invalid password format");
	    }
	    SupplierAccount supplier = getSupplierAccount(login);
	   
	    if (!encoder.matches(oldPassword, supplier.getHash())) {
	        log.warn("Incorrect old password for supplier: {}", login);
	        throw new PasswordNotValidException("Incorrect old password");
	    }

	    log.info("Old password verified for supplier: {}", login);
	    if (isPasswordFromLast(newPassword, supplier.getLastHash())) {
	        log.warn("New password was previously used for supplier: {}", login);
	        throw new PasswordNotValidException("New password should not match the previous ones");
	    }
	    List<String> lastHash = supplier.getLastHash();
	    if (lastHash.size() == n_last_hash) {
	        lastHash.remove(0); 
	    }
	    lastHash.add(supplier.getHash());
	    supplier.setHash(encoder.encode(newPassword));
	    supplier.setActivationDate(LocalDateTime.now());
	    repo.save(supplier);
	    log.info("Password updated successfully for supplier: {}", login);
	    return true;
	}



	private boolean isPasswordFromLast(String newPassword, List<String> lastHash) {
	    return lastHash.stream().anyMatch(p -> encoder.matches(newPassword, p));
	}


	@Transactional
	@Override
    @PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public boolean updateUser(String login, SupplierRequestDto user) {

		SupplierAccount farmer = getSupplierAccount(login);

		if (user.email() != null)
			farmer.setEmail(user.email());
		if (user.companyName() != null)
			farmer.setCompanyName(user.companyName());
		if (user.companyAddress() != null)
			farmer.setCompanyAddress(user.companyAddress());
		if (user.taxId() != null)
			farmer.setTaxId(user.taxId());
		if (user.contactPerson() != null)
			farmer.setContactPerson(user.contactPerson());
		if (user.phone() != null)
			farmer.setPhone(user.phone());

		if (user.password() != null) {
			String hashedPassword = encoder.encode(user.password());
			farmer.setHash(hashedPassword);
		}

		repo.save(farmer);
		return true;
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean revokeAccount(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		if (farmer.isRevoked()) {
			throw new AccountRevokeException(login);
		}
		farmer.setRevoked(true);
		repo.save(farmer);
		return true;
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean activateAccount(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		if (!farmer.isRevoked())
			throw new AccountActivationException(login);
		farmer.setRevoked(false);
		farmer.setActivationDate(LocalDateTime.now());
		repo.save(farmer);
		return true;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public RolesResponseDto getRoles(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : new RolesResponseDto(login, farmer.getRoles());
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean addRole(String login, String role) {
		SupplierAccount farmer = getSupplierAccount(login);

		Set<String> roles = farmer.getRoles();
		if (roles.contains(role))
			throw new RoleExistsException(role);
		roles.add(role);
		repo.save(farmer);
		return true;

	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean removeRole(String login, String role) {
		SupplierAccount farmer = getSupplierAccount(login);

		Set<String> roles = farmer.getRoles();
		if (!roles.contains(role))
			throw new RoleNotExistsException(role);
		roles.remove(role);
		repo.save(farmer);
		return true;
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public String getPasswordHash(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : farmer.getHash();
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public LocalDateTime getActivationDate(String login) {
		SupplierAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : farmer.getActivationDate();
	}

}
