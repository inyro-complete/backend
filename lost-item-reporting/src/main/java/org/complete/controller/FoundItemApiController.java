package org.complete.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.founditem.dto.request.AddFoundItemRequest;
import org.complete.founditem.dto.request.UpdateFoundItemRequest;
import org.complete.founditem.dto.response.FoundItemListResponse;
import org.complete.founditem.dto.response.FoundItemResponse;
import org.complete.founditem.service.FoundItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
public class FoundItemApiController {

    private final FoundItemService foundItemService;


    /** 분실물 등록 API*/
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FoundItemResponse> addFoundItem(
            @RequestHeader("Authorization") String authHeader,
            @Valid @ModelAttribute AddFoundItemRequest request) {
        return ResponseEntity.ok(foundItemService.addFoundItem(authHeader, request));
    }

    /**분실물 수정 API*/
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<FoundItemResponse> updateFoundItem(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateFoundItemRequest request,
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(foundItemService.updateFoundItem(id, request, authHeader));
    }

    /**분실물 삭제 API*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoundItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        foundItemService.deleteFoundItem(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    /**전체 분실물 목록 조회 API (페이징)*/
    @GetMapping
    public ResponseEntity<Page<FoundItemListResponse>> getAllFoundItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.getAllFoundItems(page, size));
    }

    /**특정 분실물 상세 조회 API*/
    @GetMapping("/{id}")
    public ResponseEntity<FoundItemResponse> getFoundItem(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItem(id));
    }

    /**제목으로 분실물 검색 API (페이징)*/
    @GetMapping("/search")
    public ResponseEntity<Page<FoundItemListResponse>> searchByTitle(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(foundItemService.searchByTitle(name, page, size));
    }
}
