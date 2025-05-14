package org.complete.founditem.service;

import lombok.RequiredArgsConstructor;
import org.complete.founditem.entity.FoundItem;
import org.complete.founditem.dto.request.AddFoundItemRequest;
import org.complete.founditem.dto.request.UpdateFoundItemRequest;
import org.complete.founditem.dto.response.FoundItemResponse;
import org.complete.founditem.dto.response.FoundItemListResponse;
import org.complete.founditem.repository.FoundItemRepository;
import org.complete.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final ImageService imageService;

    /**
     * 습득물 등록
     *
     * @param userId 로그인한 사용자 ID
     * @param request 습득물 등록 요청 DTO
     * @return 등록된 습득물 정보
     */
    public FoundItemResponse addFoundItem(Long userId, AddFoundItemRequest request) {
        // 이미지 업로드 처리
        String imageUrl = imageService.uploadImage(request.getImage());

        // 엔티티 생성 및 저장
        FoundItem foundItem = FoundItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .foundLocation(request.getFoundLocation())
                .foundDate(request.getFoundDate())
                .storageLocation(request.getStorageLocation())
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
     *
     * @param id 습득물 ID
     * @param request 수정 요청 DTO
     * @param userId 로그인한 사용자 ID
     * @return 수정된 습득물 정보
     */
    public FoundItemResponse updateFoundItem(Long id, UpdateFoundItemRequest request, Long userId) {
        FoundItem foundItem = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "습득물을 찾을 수 없습니다."));

        // ✅ 본인 글인지 확인
        if (!foundItem.getFinderId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 등록한 습득물만 수정할 수 있습니다.");
        }

        // 이미지가 새로 들어온 경우만 업로드
        String imageUrl = foundItem.getImageUrl();
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = imageService.uploadImage(request.getImage());
        }

        // 엔티티 필드 수정
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
     *
     * @param id 삭제할 습득물 ID
     * @param userId 로그인한 사용자 ID
     */
    public void deleteFoundItem(Long id, Long userId) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "습득물을 찾을 수 없습니다."));

        // 본인이 등록한 글인지 확인
        if (!item.getFinderId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 등록한 습득물만 삭제할 수 있습니다.");
        }

        foundItemRepository.delete(item);
    }

    /**
     * 전체 습득물 목록 조회 (페이징)
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 습득물 목록 페이지
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
     * 습득물 상세 조회
     *
     * @param id 습득물 ID
     * @return 습득물 상세 정보
     */
    public FoundItemResponse getFoundItem(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "습득물을 찾을 수 없습니다."));

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
     * 제목 기반 검색 (페이징)
     *
     * @param title 제목 키워드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색된 습득물 목록 페이지
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
}
