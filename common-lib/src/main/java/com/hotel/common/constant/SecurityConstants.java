package com.hotel.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {
  public static final String IDEMPOTENCY_KEY = "Idempotency-Key";
  public static final String AUTHORIZATION_KEY = "Authorization";
  public static final String BEARER = "Bearer ";

  public static final String CLAIM_USER_ID = "userId";
  public static final String CLAIM_ROLE = "role";
  public static final String ROLE_PREFIX = "ROLE_";
}
