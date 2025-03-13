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
	
	private static final String ADMIN_ROLE = "ADMIN";
    private static final String SELLER_ROLE = "SELLER";
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String SELF_OR_ADMIN = "#login == authentication.name or hasRole('ADMIN')";

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
				.requestMatchers("/customer/auth/**", "/seller/auth/**", "/admin/auth/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/seller/register", "/customer/register").permitAll()
				.requestMatchers("/seller/revoke/*", "/customer/revoke/*", "/seller/activate/*",
						"/customer/activate/*").hasRole(ADMIN_ROLE)
				.requestMatchers(HttpMethod.GET, "/customer/roles/{login}", "/seller/roles/{login}", 
						"/customer/password/{login}", "/seller/password/{login}",
						"/customer/activation/{login}", "/seller/activation/{login}").hasRole(ADMIN_ROLE)

				.requestMatchers(HttpMethod.PUT, "/customer/password", "/seller/password").authenticated()
				.requestMatchers(HttpMethod.DELETE, "/seller/{login}", "/customer/{login}")
				.access(new WebExpressionAuthorizationManager(SELF_OR_ADMIN))
				.requestMatchers(HttpMethod.PUT, "/seller/{login}", "/customer/{login}")
				.access(new WebExpressionAuthorizationManager(SELF_OR_ADMIN))
				.requestMatchers(HttpMethod.GET, "/seller/{login}", "/customer/{login}")
				.access(new WebExpressionAuthorizationManager(SELF_OR_ADMIN))
				.requestMatchers(HttpMethod.GET, "/surprise-bag/byPrice").hasRole(ADMIN_ROLE)
				.requestMatchers(HttpMethod.POST, "/seller/surprise-bag", "/seller/product").hasRole(SELLER_ROLE)
			    .requestMatchers(HttpMethod.DELETE, "/seller/surprise-bag/**").hasAnyRole(SELLER_ROLE, ADMIN_ROLE)
			    .requestMatchers(HttpMethod.GET, "/seller/surprise-bag/**").hasAnyRole(SELLER_ROLE, ADMIN_ROLE)
			    .requestMatchers(HttpMethod.PUT, "/seller/surprise-bag/**").hasAnyRole(SELLER_ROLE, ADMIN_ROLE)
			    .requestMatchers(HttpMethod.POST, "/customer/surprise-bag/**").hasAnyRole(SELLER_ROLE, ADMIN_ROLE)
			    .requestMatchers(HttpMethod.GET, "/customer/surprise-bag/**").hasAnyRole(SELLER_ROLE, ADMIN_ROLE)
			    .requestMatchers(HttpMethod.DELETE,"/seller/product/**").hasRole(SELLER_ROLE)
			    .requestMatchers(HttpMethod.GET,"/seller/product/**").hasRole(SELLER_ROLE)
			    .requestMatchers(HttpMethod.PUT,"/seller/product/**").hasRole(SELLER_ROLE)
			    .requestMatchers(HttpMethod.POST, "/customer/surprise-bag/{bagId}").hasAnyRole(CUSTOMER_ROLE, SELLER_ROLE, ADMIN_ROLE)
                .requestMatchers(HttpMethod.GET, "/customer/surprise-bag/{bagId}").hasAnyRole(CUSTOMER_ROLE, SELLER_ROLE, ADMIN_ROLE)
				.anyRequest().authenticated());
		return http.build();
	}

}