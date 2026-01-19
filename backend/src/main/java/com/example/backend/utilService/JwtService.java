package com.example.backend.utilService;

import com.example.backend.common.EncryptAndDecrypt;
import com.example.backend.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public String refreshToken(UserDetails userDetails){
        return refreshJwtToken(userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(EncryptAndDecrypt.encrypt(userDetails.getUsername())) && !isTokenExpired(token));
    }

    public String attributeValue(String token, String attributeName){
        String jwt = token.substring(7);
        Claims claims = extractAllClaims(jwt);
        return EncryptAndDecrypt.decrypt(claims.get(attributeName, String.class));
    }

    public String getAuthorization(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorizationHeader)){
            authorizationHeader=request.getHeader("Authorization");
        }
        return authorizationHeader;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
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

    private String refreshJwtToken(UserDetails userDetails){
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
            put("email", EncryptAndDecrypt.encrypt(user.getEmail()));
            put("roles", user.getRoleId());
            put("privileges", user.getPrivilageId());
            put("LoginTime", formattedDateTime);
            put("loginTime", LocalDateTime.now().toString());
        }};
    }
}
