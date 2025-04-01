package com.example.gatewayserver.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private String secret = "b4f8a1d7e2c9f04b85a37d6c0e9f5b12c3d4e8a7f1b6c025d9e7a43f08b2c6d1";

    private SecretKey secretKey;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @PostConstruct
    public void init() {
        String encodedKey = Base64.getEncoder().encodeToString(secret.getBytes());
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        if (decodedKey.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(decodedKey, 0, paddedKey, 0, decodedKey.length);
            decodedKey = paddedKey;
        }

        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractEmailFromToken(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            String tokenToValidate = token.startsWith("Bearer ") ? token.substring(7) : token;

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(tokenToValidate)
                    .getBody();

            // 토큰 만료 시간 확인
            Date expiration = claims.getExpiration();
            System.out.println("Token expiration: " + expiration);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            // 1. Authorization 헤더 존재 여부 확인
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Missing or invalid Authorization header.");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 2. 토큰 추출 및 검증
            if (!validateToken(authHeader)) {
                System.out.println("JWT validation failed.");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 3. 토큰에서 사용자 이메일 추출
            String email = extractEmailFromToken(authHeader);

            // 4. 사용자 정보를 요청 헤더에 추가
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    public static class Config {

    }
}
