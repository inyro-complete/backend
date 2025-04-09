package org.complete.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    public final JwtProperties jwtProperties;
    public final UserRepository userRepository;

    // JWT 토큰 생성 메소드
    public String generateToken(User user, Duration expiredAt) {
        // 현재 시간 기준으로 토큰 만료 시간 계산
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());

        // JWT 토큰 생성
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)             // 헤더에 JWT 타입 설정
                .setIssuer(jwtProperties.getIssuer())                     // 토큰 발급자(issuer) 설정
                .setIssuedAt(now)                                         // 토큰 발급 시간
                .setExpiration(expiry)                                    // 토큰 만료 시간
                .setSubject(user.getEmail())                              // 토큰 주제(subject): 유저 이메일
                .claim("id", user.getId())                             // 커스텀 클레임: 유저 ID 포함
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // HMAC-SHA256으로 서명
                .compact();                                               // 최종 토큰 문자열로 압축
    }

    // 유효한 토큰인지 검사
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 서명 검증을 위해 비밀 키 설정
                    .parseClaimsJws(token); // 토큰의 서명 부분을 파싱하고 유효성 검사 (서명 부분이 변경되지 않았는지 확인)
            return true; // 검증 성공
        } catch (Exception e) {
            return false; // 검증 실패
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // JWT 토큰에서 클레임 추출
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
    }

    // JWT에서 사용자 ID를 추출하는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token); // 토큰에서 claims를 추출
        return claims.get("id", Long.class); // "id" 값을 Long 타입으로 변환하여 반환
    }

    // JWT를 파싱하여 Claims를 추출하는 메서드
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}