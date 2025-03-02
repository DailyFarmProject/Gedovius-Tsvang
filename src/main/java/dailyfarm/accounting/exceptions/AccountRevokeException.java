package dailyfarm.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountRevokeException extends RuntimeException
{
	public AccountRevokeException(String login)
	{
		super("Account with login " + login + " is already revoked");
	}
}
