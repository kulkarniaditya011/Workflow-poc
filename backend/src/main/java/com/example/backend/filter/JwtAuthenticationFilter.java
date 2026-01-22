package com.example.backend.filter;

import com.example.backend.config.jwt.JwtAuthenticationEntryPoint;
import com.example.backend.utilService.JwtService;
import com.example.backend.utilService.SecurityUser;
import com.example.backend.utilService.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final JwtAuthenticationEntryPoint entryPoint;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            if(!StringUtils.isEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails username = userService.userDetailsService().loadUserByUsername(email);
                if (jwtService.isTokenValid(token, username)) {
                    List<GrantedAuthority> authorities =
                            jwtService.extractAuthorities(token);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username, null, authorities);

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    request.setAttribute("token", "Token invalid");
                }
            }
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            request.setAttribute("token", "Token invalid");
        } catch (ExpiredJwtException e) {
            request.setAttribute("token", "Token expired");
        } catch (Exception e) {
            request.setAttribute("token", e.getMessage());
            log.info("exception: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }


}
