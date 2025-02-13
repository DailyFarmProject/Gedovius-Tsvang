package dailyfarm.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountActivationException extends RuntimeException
{
	public AccountActivationException(String login)
	{
		super("Account with login " + login + " is already activated");
	}
}
