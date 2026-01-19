package com.example.backend.filter;

import com.example.backend.common.EncryptAndDecrypt;
import com.example.backend.utilService.UserService;
import com.example.backend.utilService.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if(StringUtils.isEmpty(authHeader)|| !StringUtils.startsWith(authHeader, "Bearer ") ){
            filterChain.doFilter(request,response);
            return;
        }
        jwt=authHeader.substring(7);

        try{
            userEmail= EncryptAndDecrypt.decrypt(jwtService.extractUserName(jwt));
            if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails= userService.userDetailsService().loadUserByUsername(userEmail);
                if(jwtService.isTokenValid(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    request.setAttribute("Token", "token invalid");
                }
            }
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            request.setAttribute("token", "Invalid token");
        }catch (ExpiredJwtException e){
            request.setAttribute("token", "Token expired");
        }catch (Exception e){
            request.setAttribute("token", "Token error");
        }
            filterChain.doFilter(request,response);
    }
}
