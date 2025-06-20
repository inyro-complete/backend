package org.complete.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.complete.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter { // 요청(Request) 하나당 한 번만 실행되는 필터
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        log.info("Authorization Header: {}", authorizationHeader);

        String token = getAccessToken(authorizationHeader);
        log.info("Extracted Token: {}", token);

        if (token != null && tokenProvider.validToken(token)) {
            log.info("Token is valid");
            Authentication authentication = tokenProvider.getAuthentication(token);
            log.info("Authentication object created: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication set in SecurityContextHolder");
        } else {
            log.warn("Token is invalid or missing");
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}