package org.complete.founditem.controller;

import lombok.RequiredArgsConstructor;
import org.complete.founditem.dto.request.AddFoundItemRequest;
import org.complete.founditem.dto.request.UpdateFoundItemRequest;
import org.complete.founditem.dto.response.FoundItemListResponse;
import org.complete.founditem.dto.response.FoundItemResponse;
import org.complete.founditem.service.FoundItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
public class FoundItemApiController {

    private final FoundItemService foundItemService;

    // 습득물 등록
    @PostMapping
    public ResponseEntity<FoundItemResponse> addFoundItem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddFoundItemRequest request) {
        return ResponseEntity.ok(foundItemService.addFoundItem(authHeader, request));
    }

    // 습득물 수정
    @PutMapping("/{id}")
    public ResponseEntity<FoundItemResponse> updateFoundItem(
            @PathVariable Long id,
            @RequestBody UpdateFoundItemRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(foundItemService.updateFoundItem(id, request, authHeader));
    }

    // 습득물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        foundItemService.deleteFoundItem(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    // 전체 습득물 조회
    @GetMapping
    public ResponseEntity<Page<FoundItemListResponse>> getAllFoundItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.getAllFoundItems(page, size));
    }

    // 습득물 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<FoundItemResponse> getFoundItem(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItem(id));
    }

    // 습득물 중에서 이름으로 조회
    @GetMapping("/search")
    public ResponseEntity<Page<FoundItemListResponse>> searchByTitle(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.searchByTitle(name, page, size));
    }
}