package dailyfarm.accounting.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey() {
    	Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        return key;
    }

    public String generateToken(String id, String email) {
        return Jwts.builder()
                .subject(id)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, String email) {
        return extractUserEmail(token).equals(email) && !isTokenExpired(token);
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

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    @SuppressWarnings("deprecation")
  	private Claims extractAllClaims(String token) {
          return Jwts.parser() 
                  .setSigningKey(getSigningKey())  
                  .build().parseClaimsJws(token).getBody();  
      }
}
