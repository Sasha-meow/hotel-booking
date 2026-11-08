package com.hotel.common.security;

import com.hotel.common.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

/** Утилита для работы с JWT (парсинг и извлечение данных) */
public class JwtUtils {
  private JwtUtils() {}

  public static SecretKey getSigningKey(String secret) {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public static Claims extractAllClaims(String token, String secret) {
    return Jwts.parser()
        .verifyWith(getSigningKey(secret))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public static String extractUsername(Claims claims) {
    return claims.getSubject();
  }

  public static String extractRole(Claims claims) {
    return claims.get(SecurityConstants.CLAIM_ROLE, String.class);
  }

  public static Long extractUserId(Claims claims) {
    return claims.get(SecurityConstants.CLAIM_USER_ID, Long.class);
  }
}
