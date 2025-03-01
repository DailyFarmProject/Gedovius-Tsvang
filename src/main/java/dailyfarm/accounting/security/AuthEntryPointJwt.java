package dailyfarm.accounting.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.error("Unauthorized error: {} - {} at {}", authException.getClass().getSimpleName(), 
                authException.getMessage(), request.getRequestURI());	    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String jsonResponse = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", 
                                                    authException.getMessage());
                response.getWriter().write(jsonResponse);
	}

}
