package dailyfarm.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class RoleNotExistsException extends RuntimeException
{
	public RoleNotExistsException(String role)
	{
		super("Role " + role + " is not exists");
	}
}
