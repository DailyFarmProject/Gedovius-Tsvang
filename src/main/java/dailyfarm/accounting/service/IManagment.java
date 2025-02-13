package dailyfarm.accounting.service;

import java.time.LocalDateTime;

import dailyfarm.accounting.dto.RolesResponseDto;

public interface IManagment <R ,T> {


	R registration(T user);

	R removeUser(String login);

	R getUser(String login);

    boolean updatePassword(String login, String password);
    
    boolean updateUser(String login, T user);

    boolean revokeAccount(String login);

    boolean activateAccount(String login);

    RolesResponseDto getRoles(String login);

    boolean addRole(String login, String role);

    boolean removeRole(String login, String role);

    String getPasswordHash(String login);

    LocalDateTime getActivationDate(String login);
}
