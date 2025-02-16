package dailyfarm.accounting.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class AuthorizationConfigurations {

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/supplier/register", "/customer/register").permitAll()
            .requestMatchers("/supplier/revoke/*", "/customer/revoke/*", "/supplier/activate/*", "/customer/activate/*")
                .hasRole("ADMIN")
            .requestMatchers("/customer/{login}/role/{role}", "/supplier/{login}/role/{role}")
                .hasRole("ADMIN")    
            .requestMatchers(HttpMethod.PUT, "/supplier/{login}", "/customer/{login}")
                .access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
            .requestMatchers(HttpMethod.DELETE, "/supplier/{login}", "/customer/{login}")
                .hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/supplier/{login}", "/customer/{login}")
                .access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMIN')"))
            .requestMatchers(HttpMethod.PUT, "/customer/password", "/supplier/password")
                .authenticated()
            .anyRequest().authenticated());

        return http.build();
    }
}
