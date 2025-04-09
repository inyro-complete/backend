package org.complete.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.dto.ApiResponse;
import org.complete.dto.LoginRequest;
import org.complete.dto.SignupRequest;
import org.complete.dto.TokenResponse;
import org.complete.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("User registered successfully."));
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenResponse);
    }

    // 로그아웃 API (블랙 리스트 방식으로 수정해야함)
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody Map<String, String> requestBody,
                                              HttpServletRequest request, HttpServletResponse response) {
        authService.logout(requestBody.get("refreshToken"), request, response);

        return ResponseEntity.ok(new ApiResponse("Logged out successfully."));
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 API
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> requestBody) {
        String newAccessToken = authService.refreshAccessToken(requestBody.get("refreshToken"));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
