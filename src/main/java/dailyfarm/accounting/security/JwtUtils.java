package dailyfarm.accounting.security;

import java.util.function.Function;
import java.util.*;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUtils {

	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey getSigningKey() {
    	if (secretKey == null || secretKey.isEmpty()) {
            log.error("JWT secret key is not configured!");
            throw new IllegalStateException("JWT secret key is missing");
    }
    	return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    } 	
    
//    @PostConstruct
//    public void init() {
//        System.out.println("JWT Secret Key: " + secretKey);
//        System.out.println("JWT Expiration Time: " + expirationTime);
//    }

    public String generateToken(String id, String email, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", roles);
        return Jwts.builder()
                .claims(claims)
                .subject(id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }
    public String generateToken(String id, String email) {
        return generateToken(id, email, Collections.emptySet());
    }
    
    public boolean isTokenValid(String token, String login) {
        try {
            String extractedId = extractUserId(token);
            if (!extractedId.equals(login)) {
                log.warn("Token ID mismatch: expected {}, got {}", login, extractedId);
                return false;
            }
            if (isTokenExpired(token)) {
                log.warn("Token expired for login: {}", login);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
    	
   
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
	}
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

	public String extractUserEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", Set.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }
}
