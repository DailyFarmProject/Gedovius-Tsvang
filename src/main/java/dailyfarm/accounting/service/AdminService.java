package dailyfarm.accounting.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.entity.admin.AdminAccount;
import dailyfarm.accounting.exceptions.UserNotFoundException;
import dailyfarm.accounting.repository.admin.AdminRepository;
import dailyfarm.accounting.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final AdminRepository repo;


    
    public TokenResponseDto login(LoginRequestDto request) {
        log.info("Admin login attempt: {}", request.login());
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));

		AdminAccount admin = repo.findByLogin(request.login()).orElseThrow(() -> new UserNotFoundException(request.login()));

		String token = jwtUtils.generateToken(admin.getLogin(), admin.getEmail(), admin.getRoles());
		log.info("Login successful, token generated for admin: {}", request.login());
		return new TokenResponseDto(token);
    }
}

