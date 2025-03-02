package dailyfarm.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordNotValidException extends RuntimeException{
	public PasswordNotValidException(String password) {
		super("Password " + password + " is not valid");
	}
}
