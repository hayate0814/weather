package com.jp.kinto.app.bff.core.security.jwt;

import static com.jp.kinto.app.bff.core.constant.Constant.JPN_KINTO_ID_AUTH;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenProvider {
  private static final Long EXPIRE_DAYS = 100000L;
  private Algorithm algorithm;

  public JwtTokenProvider(String secretKey) {
    algorithm = Algorithm.HMAC256(secretKey);
  }

  public String createToken(AuthUser user) {
    return createToken(user, EXPIRE_DAYS);
  }

  public String createToken(AuthUser user, Long expiredAfterDays) {
    var map =
        new HashMap<String, Object>() {
          {
            put("user_id", user.getUserId());
            put("role", user.getRole().name());
            put("member_id", user.getMemberId());
            put("member_type", user.getMemberType() != null ? user.getMemberType().name() : null);
            put(JPN_KINTO_ID_AUTH, user.getJpnKintoIdAuth());
          }
        };

    return JWT.create()
        .withPayload(map)
        .withExpiresAt(
            Date.from(
                LocalDateTime.now()
                    .plusDays(expiredAfterDays)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
        .sign(algorithm);
  }

  public AuthUser parseToken(String token) {
    var jwt = JWT.require(algorithm).build().verify(token);
    var claims = jwt.getClaims();
    return AuthUser.builder()
        .userId(claims.get("user_id").asInt())
        .role(AuthUser.Role.valueOf(claims.get("role").asString()))
        .memberId(stringClaim(claims, "member_id"))
        .jpnKintoIdAuth(stringClaim(claims, JPN_KINTO_ID_AUTH))
        .memberType(Constant.MemberType.fromString(stringClaim(claims, "member_type")))
        .build();
  }

  private String stringClaim(Map<String, Claim> claims, String name) {
    var v = claims.get(name);
    return v.isNull() ? null : v.asString();
  }
}
