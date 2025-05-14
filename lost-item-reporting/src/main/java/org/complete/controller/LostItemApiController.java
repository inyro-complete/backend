package org.complete.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.domain.LostItem;
import org.complete.domain.User;
import org.complete.dto.AddLostItemRequest;
import org.complete.dto.LostItemResponse;
import org.complete.dto.LostItemListResponse;
import org.complete.dto.UpdateLostItemRequest;
import org.complete.service.LostItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LostItemApiController {

    public final LostItemService lostItemService;

    // 분실물 등록 API
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/lost-items")
    public ResponseEntity<LostItem> addLostItem(@AuthenticationPrincipal User user,
                                                @Valid @ModelAttribute AddLostItemRequest request) {

        LostItem savedLostItem = lostItemService.addLostItem(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLostItem);
    }

    // 분실물 전체 목록 조회 API
    @GetMapping("/api/lost-items/list")
    public ResponseEntity<Page<LostItemListResponse>> findAllLostItems(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {

        Page<LostItemListResponse> lostItemsList = lostItemService.findAll(page, size);

        return ResponseEntity.ok()
                .body(lostItemsList);
    }

    // 분실물 상세 조회 API
    @GetMapping("/api/lost-items/{lostItemId}")
    public ResponseEntity<LostItemResponse> findLostItem(@PathVariable Long lostItemId) {

        LostItem lostItem = lostItemService.findById(lostItemId);

        return ResponseEntity.ok()
                .body(new LostItemResponse(lostItem));
    }

    // 분실물 이름으로 목록 조회 API
    @GetMapping("/api/lost-items")
    public ResponseEntity<Page<LostItemListResponse>> findLostItemsByName(@RequestParam String title,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {

        Page<LostItemListResponse> lostItemsList = lostItemService.findByTitle(title, page, size);

        return ResponseEntity.ok()
                .body(lostItemsList);
    }

    // 분실물 삭제 API
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/api/lost-items/{lostItemId}")
    public ResponseEntity<Void> deleteLostItem(@PathVariable Long lostItemId,
                                               @AuthenticationPrincipal User user) {

        lostItemService.delete(lostItemId, user.getId());

        return ResponseEntity.ok().build();
    }


    // 분실물 수정 API
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/api/lost-items/{lostItemId}")
    public ResponseEntity<LostItem> updateLostItem(@PathVariable Long lostItemId,
                                                   @AuthenticationPrincipal User user,
                                                   @Valid @ModelAttribute UpdateLostItemRequest request) {

        LostItem updatedLostItem = lostItemService.update(lostItemId, user.getId(), request);

        return ResponseEntity.ok(updatedLostItem); // 수정된 분실물 반환
    }
}