package com.auth.service.service;

import com.auth.service.constants.Constants;
import com.auth.service.model.User;
import com.auth.service.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public static String token;

    private final RoleRepository roleRepository;

    @Autowired
    public JwtService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    //    public String extractUsername(String token) {
//        Claims claims = extractAllClaims(token);
//        String subject = claims.getSubject();
//        String email = claims.get(Constants.EMAIL, String.class);
//        String mobile = claims.get(Constants.MOBILE_NUMBER, String.class);
//        return subject != null ? subject : (email != null ? email : mobile);
//    }
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get(Constants.EMAIL, String.class);
        String mobile = claims.get(Constants.MOBILE_NUMBER, String.class);

        // Match the logic in User.getUsername()
        return (email != null && !email.isEmpty()) ? email : mobile;
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User user, String source) {
        return generateToken(new HashMap<>(), user, source);
    }

    public String generateToken(Map<String, Object> extraClaims, User user, String source) {
        return buildToken(extraClaims, user, jwtExpiration, source);
    }

    public String generateRefreshToken(User user, String source) {
        return buildToken(new HashMap<>(), user, refreshExpiration, source);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration, String source) {
        extraClaims.put("id", user.getId());
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("role", user.getRole().getRoleName());
        extraClaims.put("isUpdated", user.getIsUpdated());
        if (user.getRole() != null) {
            extraClaims.put("permissions", user.getRole().getPermissions()
                    .stream()
                    .map(permission -> permission.getName())
                    .collect(Collectors.toList()));
        } else {
            extraClaims.put("permissions", Collections.emptyList());
        }
        token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .claim(Constants.EMAIL, user.getEmail())
                .claim(Constants.MOBILE_NUMBER, user.getMobileNumber())
//                .claim(Constants.ROLE,user.getRole().getRoleName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }


//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        System.out.println(token);
//        System.out.println(userDetails);
//        final String username = extractUsername(token);
//        System.out.println(username);
//        System.out.println(userDetails.getUsername());
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String tokenUsername = extractUsername(token);
        final String userUsername = userDetails.getUsername();

        System.out.println("Token username: " + tokenUsername);
        System.out.println("UserDetails username: " + userUsername);

        if (isMobileFormat(tokenUsername)) {
            return tokenUsername.equals(((User) userDetails).getMobileNumber()) && !isTokenExpired(token);
        } else {
            return tokenUsername.equals(((User) userDetails).getEmail()) && !isTokenExpired(token);
        }
    }

    private boolean isMobileFormat(String input) {
        return input != null && input.matches("^\\d{10}$");
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractRole(String token) {
        try {
            return extractClaim(token, claims -> claims.get(Constants.ROLE, String.class));
        } catch (Exception e) {
            return null;
        }
    }
}
