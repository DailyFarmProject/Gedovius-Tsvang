package dailyfarm.accounting.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final AuthEntryPointJwt unauthorizedHandler;
	private final JwtAuthenticationFilter authFilter;
	
	@Bean
	AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(authFilter,UsernamePasswordAuthenticationFilter.class);

		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/supplier/register", "/customer/register").permitAll()
				.requestMatchers("/supplier/revoke/*", "/customer/revoke/*", "/supplier/activate/*",
						"/customer/activate/*").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/customer/roles/{login}", "/supplier/roles/{login}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/customer/password/{login}", "/supplier/password/{login}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/customer/activation/{login}", "/supplier/activation/{login}").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/supplier/{login}", "/customer/{login}").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/supplier/{login}", "/customer/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
				.requestMatchers(HttpMethod.GET, "/supplier/{login}", "/customer/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
				.requestMatchers(HttpMethod.PUT, "/customer/password", "/supplier/password").authenticated()
				.anyRequest().authenticated());

		return http.build();
	}

}