package dailyfarm.accounting.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = request.getHeader("Authorization");

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);

			try {
			    String login = jwtUtils.extractUserId(token);
			    UserDetails userDetails = userDetailsService.loadUserByUsername(login);
			    if (jwtUtils.isTokenValid(token, userDetails.getUsername())
			            && SecurityContextHolder.getContext().getAuthentication() == null) {
			        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			                userDetails, null, userDetails.getAuthorities());
			        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			        SecurityContextHolder.getContext().setAuthentication(authentication);
			        log.debug("Authentication set for user: {}", userDetails.getUsername());
			    } else {
			        log.warn("Invalid token for user: {}", userDetails.getUsername());
			        throw new JwtException("Invalid token");
			    }
			} catch (ExpiredJwtException e) {
			    log.warn("JWT token expired: {}", e.getMessage());
			    throw e;
			} catch (MalformedJwtException e) {
			    log.warn("Invalid JWT token format: {}", e.getMessage());
			    throw e;
			} catch (Exception e) {
			    log.error("Authentication error: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
			    throw e;
			}
		}
		chain.doFilter(request, response);
	}
}