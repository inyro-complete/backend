package org.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn; // 액세스 토큰의 만료 시간 (초 단위)
}