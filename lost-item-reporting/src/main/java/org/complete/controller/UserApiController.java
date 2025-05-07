package org.complete.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.dto.ApiResponse;
import org.complete.dto.DeleteUserRequest;
import org.complete.dto.UserResponse;
import org.complete.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 회원 탈퇴 API
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/api/users/me")
    public ResponseEntity<ApiResponse> deleteCurrentUser(@AuthenticationPrincipal User user,
                                                         @Valid @RequestBody DeleteUserRequest request) {

        boolean isPasswordValid = userService.checkPassword(user.getId(), request.getPassword());

        if (!isPasswordValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid password."));
        }

        userService.deleteUser(user.getId());
        return ResponseEntity.ok()
                .body(new ApiResponse("User deleted successfully."));
    }

    // 내 정보 조회 API
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserById(user.getId());
        return ResponseEntity.ok()
                .body(userResponse);
    }

    // 내가 쓴 글 목록 조회 API
//    @GetMapping("/api/users/me/posts")
//    public ResponseEntity<Page<PostListResponse>> getMyPosts(@AuthenticationPrincipal User user,
//                                                             @RequestParam(defaultValue = "0") int page,
//                                                             @RequestParam(defaultValue = "10") int size) {
//
//        Page<PostListResponse> posts = userService.getPostsByUser(user.getId(), page, size);
//
//        return ResponseEntity.ok(posts);
//    }

}
