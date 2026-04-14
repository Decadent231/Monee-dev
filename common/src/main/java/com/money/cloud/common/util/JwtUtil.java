package com.money.cloud.common.util;

import com.money.cloud.common.constant.SecurityConstants;
import com.money.cloud.common.context.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expireMillis;

    public JwtUtil(@Value("${security.jwt.secret}") String secret,
                   @Value("${security.jwt.expire-seconds}") long expireSeconds) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ignored) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expireMillis = expireSeconds * 1000;
    }

    public String generateToken(LoginUser loginUser) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .subject(String.valueOf(loginUser.getUserId()))
                .claim(SecurityConstants.USER_ID_CLAIM, loginUser.getUserId())
                .claim(SecurityConstants.EMAIL_CLAIM, loginUser.getEmail())
                .claim(SecurityConstants.NICKNAME_CLAIM, loginUser.getNickname())
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public LoginUser parseLoginUser(String token) {
        Claims claims = parseToken(token);
        Number userIdNumber = claims.get(SecurityConstants.USER_ID_CLAIM, Number.class);
        Long userId = userIdNumber == null ? Long.valueOf(claims.getSubject()) : userIdNumber.longValue();
        return new LoginUser(
                userId,
                claims.get(SecurityConstants.EMAIL_CLAIM, String.class),
                claims.get(SecurityConstants.NICKNAME_CLAIM, String.class)
        );
    }
}
