package com.dimtoups.accountingApp.core.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtHelper {

  private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
  private static final int EXPIRATION_IN_MINUTES = 60;

  public static String generateToken(String username) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(EXPIRATION_IN_MINUTES, ChronoUnit.MINUTES)))
        .signWith(SECRET_KEY)
        .compact();
  }

  public static String extractUsername(String token) {
    return getTokenBody(token).getSubject();
  }

  public static boolean isClientAuthorized(String bearerToken, String username) {
    String jwToken = bearerToken.replace("Bearer ", "");
    return JwtHelper.extractUsername(jwToken).equals(username);
  }

  public static Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private static Claims getTokenBody(String token) {
    try {
      return Jwts
          .parser()
          .verifyWith(SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      throw new AccessDeniedException("Access denied: " + e.getMessage());
    }
  }

  private static boolean isTokenExpired(String token) {
    Claims claims = getTokenBody(token);
    return claims.getExpiration().before(new Date());
  }
}
