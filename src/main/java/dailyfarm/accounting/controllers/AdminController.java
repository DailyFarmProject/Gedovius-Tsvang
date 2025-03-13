package dailyfarm.accounting.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.TokenResponseDto;
import dailyfarm.accounting.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        TokenResponseDto response = adminService.login(request);
        return ResponseEntity.ok(response);
    }
    
}
