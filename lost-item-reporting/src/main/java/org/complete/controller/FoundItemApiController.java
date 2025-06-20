package org.complete.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.founditem.dto.request.AddFoundItemRequest;
import org.complete.founditem.dto.request.UpdateFoundItemRequest;
import org.complete.founditem.dto.response.FoundItemListResponse;
import org.complete.founditem.dto.response.FoundItemResponse;
import org.complete.founditem.service.FoundItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
public class FoundItemApiController {

    private final FoundItemService foundItemService;

    // 습득물 등록
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FoundItemResponse> addFoundItem(
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute AddFoundItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(foundItemService.addFoundItem(user.getId(), request));
    }

    // 습득물 수정
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<FoundItemResponse> updateFoundItem(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateFoundItemRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(foundItemService.updateFoundItem(id, request, user.getId()));
    }

    // 습득물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        foundItemService.deleteFoundItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 전체 목록
    @GetMapping
    public ResponseEntity<Page<FoundItemListResponse>> getAllFoundItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.getAllFoundItems(page, size));
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<FoundItemResponse> getFoundItem(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItem(id));
    }

    // 제목 검색
    @GetMapping("/search")
    public ResponseEntity<Page<FoundItemListResponse>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.searchByTitle(title, page, size));
    }
}
