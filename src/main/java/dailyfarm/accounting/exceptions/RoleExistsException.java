package dailyfarm.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class RoleExistsException extends RuntimeException
{
	public RoleExistsException(String role)
	{
		super("Role " + role + " is already exists");
	}
}
