package org.complete.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.dto.LoginRequest;
import org.complete.dto.SignupRequest;
import org.complete.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    public void signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userService.isEmailExists(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
        }

        // 비밀번호와 확인용 비밀번호가 일치하는지 체크
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        // 회원 저장
        userService.createUser(request);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        // 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 인증된 사용자 정보
        User user = (User) authentication.getPrincipal();

        // 액세스 토큰 생성
        String accessToken = tokenService.generateAccessToken(user);

        // 리프레시 토큰 생성 후 DB에 저장
        String refreshToken = tokenService.generateRefreshToken(user);
        refreshTokenService.save(user.getId(), refreshToken);

        return new TokenResponse(accessToken, refreshToken, 3600L);
    }

    // 로그아웃
    public void logout(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        // 세션 종료 처리
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // 리프레시 토큰 삭제
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is missing in header.");
        }
        refreshTokenService.delete(refreshToken);
    }

    // 리프레시 토큰을 검증하고 새로운 액세스 토큰 발급
    public String refreshAccessToken(String refreshToken) {

        tokenService.validateToken(refreshToken);

        Long userId = tokenService.getUserId(refreshToken);
        User user = userService.findById(userId);
        return tokenService.generateAccessToken(user);
    }
}
