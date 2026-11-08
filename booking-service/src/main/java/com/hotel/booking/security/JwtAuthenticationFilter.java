package com.hotel.booking.security;

import com.hotel.common.constant.SecurityConstants;
import com.hotel.common.security.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Фильтр для проверки JWT в запросах */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Value("${security.jwt.secret}")
  private String secret;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_KEY);

    if (authHeader == null || !authHeader.startsWith(SecurityConstants.BEARER)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(SecurityConstants.BEARER.length());
    try {
      Claims claims = JwtUtils.extractAllClaims(token, secret);

      String username = JwtUtils.extractUsername(claims);
      String role = JwtUtils.extractRole(claims);
      Long userId = JwtUtils.extractUserId(claims);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        var authorities = List.of(new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role));

        var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);

        authToken.setDetails(username);

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (Exception ignored) {
    }

    filterChain.doFilter(request, response);
  }
}
