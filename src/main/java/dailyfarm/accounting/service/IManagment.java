package dailyfarm.accounting.service;

import java.time.LocalDateTime;

import dailyfarm.accounting.dto.LoginRequestDto;
import dailyfarm.accounting.dto.RolesResponseDto;
import dailyfarm.accounting.dto.TokenResponseDto;

public interface IManagment <R ,T> {


	R registration(T user);

	R removeUser(String login);

	R getUser(String login);

    boolean updatePassword(String login, String oldPassword, String newPassword);;
    
    boolean updateUser(String login, T user);

    boolean revokeAccount(String login);

    boolean activateAccount(String login);

    RolesResponseDto getRoles(String login);

    boolean addRole(String login, String role);

    boolean removeRole(String login, String role);

    String getPasswordHash(String login);

    LocalDateTime getActivationDate(String login);
    
    TokenResponseDto login(LoginRequestDto dto);
}
