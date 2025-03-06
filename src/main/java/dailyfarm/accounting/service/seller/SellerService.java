package dailyfarm.accounting.service.seller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.dto.seller.SellerRequestDto;
import dailyfarm.accounting.dto.seller.SellerResponseDto;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.exceptions.AccountActivationException;
import dailyfarm.accounting.exceptions.AccountRevokeException;
import dailyfarm.accounting.exceptions.PasswordNotValidException;
import dailyfarm.accounting.exceptions.RoleExistsException;
import dailyfarm.accounting.exceptions.RoleNotExistsException;
import dailyfarm.accounting.exceptions.UserExistsException;
import dailyfarm.accounting.exceptions.UserNotFoundException;
import dailyfarm.accounting.repository.seller.SellerRepository;
import dailyfarm.accounting.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerService implements ISellerManagement {

	private final SellerRepository repo;
	private final PasswordEncoder encoder;
	private final JwtUtils jwtUtils;
	private final AuthenticationManager authManager;

	@Value("${password_length:8}")
	private int passwordLength;

	@Value("${n_last_hash:3}")
	private int n_last_hash;

	@Override
	public SellerResponseDto registration(SellerRequestDto user) {

		if (repo.findByLogin(user.login()).isPresent()) {
			throw new UserExistsException(user.login());
		}
		if (!isPasswordValid(user.password()))
			throw new PasswordNotValidException(user.password());

		String hashedPassword = encoder.encode(user.password());
		SellerAccount farmer = SellerAccount.of(user);
		farmer.setHash(hashedPassword);

		log.info("Saving new supplier: {}", farmer);
		farmer = repo.save(farmer);
		log.info("Supplier saved successfully: {}", farmer.getLogin());

		return SellerResponseDto.build(farmer);
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	@Override
	public TokenResponseDto login(LoginRequestDto dto) {
		log.debug("Attempting login for user: {}", dto.login());
		authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.login(), dto.password()));
		SellerAccount farmer = getSupplierAccount(dto.login());
		String token = jwtUtils.generateToken(farmer.getLogin(), farmer.getEmail(), farmer.getRoles());
		log.info("Login successful, token generated for user: {}", dto.login());
		return new TokenResponseDto(token);
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public SellerResponseDto removeUser(String login) {
		SellerAccount farmer = getSupplierAccount(login);
		repo.deleteByLogin(login);

		return SellerResponseDto.build(farmer);
	}

	private SellerAccount getSupplierAccount(String login) {
		return repo.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
	}

	@Override
	@PreAuthorize("#login == authentication.name or hasRole('ADMIN')")
	public SellerResponseDto getUser(String login) {
		SellerAccount farmer = getSupplierAccount(login);
		return SellerResponseDto.build(farmer);
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
		SellerAccount supplier = getSupplierAccount(login);

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
	public boolean updateUser(String login, SellerRequestDto user) {

		SellerAccount farmer = getSupplierAccount(login);

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
		SellerAccount farmer = getSupplierAccount(login);
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
		SellerAccount farmer = getSupplierAccount(login);
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
		SellerAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : new RolesResponseDto(login, farmer.getRoles());
	}

	@Transactional
	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public boolean addRole(String login, String role) {
		SellerAccount farmer = getSupplierAccount(login);

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
		SellerAccount farmer = getSupplierAccount(login);

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
		SellerAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : farmer.getHash();
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public LocalDateTime getActivationDate(String login) {
		SellerAccount farmer = getSupplierAccount(login);
		return farmer.isRevoked() ? null : farmer.getActivationDate();
	}

}
