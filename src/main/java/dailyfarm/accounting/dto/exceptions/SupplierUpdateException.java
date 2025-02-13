package dailyfarm.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class SupplierUpdateException extends RuntimeException {

	public SupplierUpdateException() {
		super("Company name and address can not be blank");
	}
}
