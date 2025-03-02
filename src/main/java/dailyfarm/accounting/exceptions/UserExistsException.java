package dailyfarm.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException{
	public UserExistsException(String login) {
		super("User with login " + login + " is already exists");
	}
}
