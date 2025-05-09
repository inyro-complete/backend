package org.complete.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
//import org.complete.dto.PasswordChangeRequest;
//import org.complete.dto.PostListResponse;
import org.complete.dto.PasswordChangeRequest;
import org.complete.dto.SignupRequest;
import org.complete.dto.UserResponse;
//import org.complete.founditem.service.FoundItemService;
import org.complete.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
//    private final LostItemService lostItemService;
//    private final FoundItemService foundItemService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 존재 여부 확인
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 저장
    public void createUser(SignupRequest request) {
        userRepository.save(User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Unexpected user"));
    }

    // 회원 탈퇴 메서드
    public boolean checkPassword(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return bCryptPasswordEncoder.matches(rawPassword, user.getPassword());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Unexpected user."));
        userRepository.delete(user);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Unexpected user."));

        return new UserResponse(user);
    }

//    public Page<PostListResponse> getPostsByUser(Long userId, int page, int size) {
//        // 분실물 게시글 조회
//        List<PostListResponse> lostPosts = lostItemService.findByUserId(userId);
//
//        // 습득물 게시글 조회
//        List<PostListResponse> foundPosts = foundItemService.findByUserId(userId);
//
//        // 통합 + 최신순 정렬
//        List<PostListResponse> allPosts = new ArrayList<>();
//        allPosts.addAll(lostPosts);
//        allPosts.addAll(foundPosts);
//        allPosts.sort(Comparator.comparing(PostListResponse::getLostDate).reversed());
//
//        // 수동 페이징
//        int start = Math.min(page * size, allPosts.size());
//        int end = Math.min(start + size, allPosts.size());
//        List<PostListResponse> pageContent = allPosts.subList(start, end);
//
//        return new PageImpl<>(pageContent, PageRequest.of(page, size), allPosts.size());
//    }

    // 비밀번호 변경
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Unexpected user."));

        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 바로 패스워드 변경
        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        // 변경 감지로 자동 반영
        userRepository.save(user);
    }

}