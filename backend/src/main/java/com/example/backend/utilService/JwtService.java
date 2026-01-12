package com.example.backend.utilService;

import com.example.backend.common.EncryptAndDecrypt;
import com.example.backend.model.Privilages;
import com.example.backend.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${secret.key}")
    private String jwtSecret;

    public String generateToken(UserDetails userDetails) {
        return jwtToken(userDetails);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private String jwtToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(populateClaims(userDetails))
                .setSubject(EncryptAndDecrypt.encrypt(userDetails.getUsername()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+3600000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String,Object> populateClaims(UserDetails userDetails) {
        Users user = (Users) userDetails;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        return new HashMap<>(){{
            put("name ", EncryptAndDecrypt.encrypt(user.getUsername()));
            put("activeRole", EncryptAndDecrypt.encrypt(user.getRole().getName()));
            put("email", EncryptAndDecrypt.encrypt(user.getEmail()));
            put("userPrivilege", user.getRole().getPrivilages().stream().map(Privilages::getName).collect(Collectors.toList()));
            put("LoginTime", formattedDateTime);
        }};
    }



}
