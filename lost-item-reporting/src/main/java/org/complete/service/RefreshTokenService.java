package org.complete.service;

import lombok.RequiredArgsConstructor;
import org.complete.domain.RefreshToken;
import org.complete.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(Long userId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(userId, refreshToken));
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public void delete(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found."));

        refreshTokenRepository.delete(refreshToken);
    }
}