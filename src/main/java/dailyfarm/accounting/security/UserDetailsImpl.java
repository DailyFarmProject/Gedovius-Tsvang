package dailyfarm.accounting.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dailyfarm.accounting.entity.UserAccount;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class UserDetailsImpl implements UserDetails{
	private static final long serialVersionUID = 3833998882946921911L;
	private final Long id;
    private final String login;
    private final String hash;
    private final String email;
    private final boolean revoked;
    private final Set<GrantedAuthority> authorities;

    public UserDetailsImpl(UserAccount user) {
    	if (user == null) {
            throw new IllegalArgumentException("UserAccount cannot be null");
        }
        this.id = user.getId();
        this.login = user.getLogin();
        this.hash = user.getHash();
        this.email = user.getEmail();
        this.revoked = user.isRevoked();
        this.authorities = user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }


public static UserDetailsImpl build(UserAccount user) {
	log.debug("Building UserDetails for user: {}", user.getLogin());
    return new UserDetailsImpl(user);
}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return hash;
	}

	@Override
	public String getUsername() {
		return login;
	}
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !revoked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !revoked;
    }
    

}
