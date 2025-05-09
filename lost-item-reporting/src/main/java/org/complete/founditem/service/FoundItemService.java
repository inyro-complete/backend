package org.complete.founditem.service;

import lombok.RequiredArgsConstructor;
//import org.complete.dto.PostListResponse;
import org.complete.founditem.entity.FoundItem;
import org.complete.founditem.dto.request.AddFoundItemRequest;
import org.complete.founditem.dto.request.UpdateFoundItemRequest;
import org.complete.founditem.dto.response.FoundItemResponse;
import org.complete.founditem.dto.response.FoundItemListResponse;
import org.complete.founditem.repository.FoundItemRepository;
import org.complete.service.ImageService;
import org.complete.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final TokenService tokenService;
    private final ImageService imageService;

    /**
     * 습득물 등록
     */
    public FoundItemResponse addFoundItem(String authHeader, AddFoundItemRequest request) {
        Long userId = tokenService.getUserId(authHeader.replace("Bearer ", ""));

        // ✅ MultipartFile -> 이미지 업로드 -> URL 저장
        String imageUrl = imageService.uploadImage(request.getImage());

        FoundItem foundItem = FoundItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .foundLocation(request.getFoundLocation())
                .foundDate(request.getFoundDate())
                .storageLocation(request.getStorageLocation()) // ✅ 이 줄 추가!
                .imageUrl(imageUrl)
                .status(FoundItem.FoundItemStatus.valueOf(request.getStatus()))
                .finderId(userId)
                .build();


        FoundItem savedItem = foundItemRepository.save(foundItem);

        return new FoundItemResponse(
                savedItem.getId(),
                savedItem.getTitle(),
                savedItem.getDescription(),
                savedItem.getFoundLocation(),
                savedItem.getImageUrl(),
                savedItem.getStatus().name()
        );
    }

    /**
     * 습득물 수정
     */
    public FoundItemResponse updateFoundItem(Long id, UpdateFoundItemRequest request, String authHeader) {
        Long userId = tokenService.getUserId(authHeader.replace("Bearer ", ""));

        FoundItem foundItem = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found item not found"));

        // ✅ 기존 이미지 유지 or 새 이미지 업로드
        String imageUrl = foundItem.getImageUrl();
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = imageService.uploadImage(request.getImage());
        }

        // 업데이트 수행
        foundItem.update(
                request.getTitle(),
                request.getDescription(),
                request.getFoundLocation(),
                request.getFoundDate(),
                request.getStorageLocation(),
                request.getStorageContact(),
                request.getLoserName(),
                imageUrl,
                FoundItem.FoundItemStatus.valueOf(request.getStatus())
        );

        foundItemRepository.save(foundItem);

        return new FoundItemResponse(
                foundItem.getId(),
                foundItem.getTitle(),
                foundItem.getDescription(),
                foundItem.getFoundLocation(),
                foundItem.getImageUrl(),
                foundItem.getStatus().name()
        );
    }

    /**
     * 습득물 삭제
     */
    public void deleteFoundItem(Long id, String authHeader) {
        foundItemRepository.deleteById(id);
    }

    /**
     * 전체 습득물 조회 (페이징)
     */
    public Page<FoundItemListResponse> getAllFoundItems(int page, int size) {
        return foundItemRepository.findAll(PageRequest.of(page, size))
                .map(item -> new FoundItemListResponse(
                        item.getId(),
                        item.getTitle(),
                        item.getFoundLocation(),
                        item.getFoundDate(),
                        item.getStatus().name()
                ));
    }

    /**
     * 단일 습득물 상세 조회
     */
    public FoundItemResponse getFoundItem(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found item not found"));

        return new FoundItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getFoundLocation(),
                item.getImageUrl(),
                item.getStatus().name()
        );
    }

    /**
     * 제목 검색 기반 조회 (페이징)
     */
    public Page<FoundItemListResponse> searchByTitle(String title, int page, int size) {
        return foundItemRepository.findByTitleContaining(title, PageRequest.of(page, size))
                .map(item -> new FoundItemListResponse(
                        item.getId(),
                        item.getTitle(),
                        item.getFoundLocation(),
                        item.getFoundDate(),
                        item.getStatus().name()
                ));
    }

//    public List<PostListResponse> findByUserId(Long userId) {
//        List<FoundItem> items = foundItemRepository.findByUserId(userId);
//
//        return items.stream()
//                .map(PostListResponse::new)  // FoundItem → PostListResponse
//                .collect(Collectors.toList());
//    }

}
