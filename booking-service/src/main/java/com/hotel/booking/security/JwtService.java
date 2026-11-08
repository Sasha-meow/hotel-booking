package com.hotel.booking.security;

import com.hotel.common.constant.SecurityConstants;
import com.hotel.common.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Сервис для генерации JWT-токенов */
@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.expiration}")
  private long expiration;

  public String generateToken(Long userId, String username, String role) {
    return Jwts.builder()
        .subject(username)
        .claim(SecurityConstants.CLAIM_USER_ID, userId)
        .claim(SecurityConstants.CLAIM_ROLE, role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(JwtUtils.getSigningKey(secret))
        .compact();
  }
}
