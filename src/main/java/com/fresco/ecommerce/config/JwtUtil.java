package com.fresco.ecommerce.config;

import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.validity}")
    private Integer tokenValidity;

    @Autowired
    private UserRepo repo;

    // Called during login — creates the JWT string
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())           // store username inside token
                .claim("roles", user.getRoles().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // Called by filter on every request — checks token is valid and not expired
    public boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // Extract username from token, load full User from DB
    public User getUser(final String token) {
        Claims claims = parseClaims(token);
        return repo.findByUsername(claims.getSubject()).get();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}