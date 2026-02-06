package com.auth.service.utils;


import com.auth.service.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {
    @Value("${jwt.secret-key}")
    private String secretKey;

    public String JWTDecoder(String jwtToken) {
        // Parsing the JWT token
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // Replace "yourSecretKey" with the actual key used for signing
                .build()
                .parseClaimsJws(jwtToken);
        // Extracting and printing properties from the token
        return (String) claims.getBody().get(Constants.SOURCE);
    }

}
