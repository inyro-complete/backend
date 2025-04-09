package org.complete.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.complete.domain.LostItem;
import org.complete.dto.AddLostItemRequest;
import org.complete.dto.LostItemResponse;
import org.complete.dto.LostItemListResponse;
import org.complete.dto.UpdateLostItemRequest;
import org.complete.service.LostItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LostItemApiController {

    public final LostItemService lostItemService;

    // 분실물 등록 API
    @PostMapping("/api/lost-items")
    public ResponseEntity<LostItem> addLostItem(@RequestHeader("Authorization") String authHeader,
                                                @Valid @ModelAttribute AddLostItemRequest request) {

        LostItem savedLostItem = lostItemService.addLostItem(authHeader, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLostItem);
    }

    // 분실물 목록 조회 API
    @GetMapping("/api/lost-items/list")
    public ResponseEntity<Page<LostItemListResponse>> findAllLostItems(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {

        Page<LostItemListResponse> lostItemsList = lostItemService.findAll(page, size);

        return ResponseEntity.ok()
                .body(lostItemsList);
    }

    // 분실물 상세 조회 API
    @GetMapping("/api/lost-items/{id}")
    public ResponseEntity<LostItemResponse> findLostItem(@PathVariable long id) {

        LostItem lostItem = lostItemService.findById(id);

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

    // 분실물 글 삭제 API
    @DeleteMapping("/api/lost-items/{id}")
    public ResponseEntity<Void> deleteLostItem(@PathVariable long id, @RequestHeader("Authorization") String authHeader) {

        lostItemService.delete(id, authHeader);

        return ResponseEntity.ok()
                .build();
    }

    // 분실물 글 수정 API
    @PutMapping("/api/lost-items/{id}")
    public ResponseEntity<LostItem> updateLostItem(@PathVariable long id, @Valid @ModelAttribute UpdateLostItemRequest request,
                                                   @RequestHeader("Authorization") String authHeader) {

        LostItem updatedLostItem = lostItemService.update(id, request, authHeader);

        return ResponseEntity.ok(updatedLostItem); // 수정된 분실물 반환
    }
}