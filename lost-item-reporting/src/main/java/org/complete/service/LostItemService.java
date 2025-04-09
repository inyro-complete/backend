package org.complete.service;

import lombok.RequiredArgsConstructor;
import org.complete.domain.LostItem;
import org.complete.dto.AddLostItemRequest;
import org.complete.dto.LostItemListResponse;
import org.complete.dto.UpdateLostItemRequest;
import org.complete.repository.LostItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;
    private final TokenService tokenService;
    private final ImageService imageService;

    // 인가를 확인한 후 분실물 정보를 저장하는 메서드
    public LostItem addLostItem(String authHeader, AddLostItemRequest request) {

        String accessToken = authHeader.replace("Bearer ", "");
        tokenService.validateToken(accessToken);

        String imageUrl = imageService.uploadImage(request.getImageFile());

        return lostItemRepository.save(LostItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .lostLocation(request.getLostLocation())
                .lostDate(request.getLostDate())
                .imageUrl(imageUrl)
                .status("FINDING") // 기본 값으로 'FINDING' 설정
                .loserId(tokenService.getUserId(accessToken))
                .build());
    }

    // 분실물 목록을 페이지 단위로 조회하는 메서드
    public Page<LostItemListResponse> findAll(int page, int size) {
        // 페이지 번호가 1보다 작으면 0으로, size가 0 이하이면 10으로 설정
        Pageable pageable = PageRequest.of(page < 1 ? 0 : page - 1, size <= 0 ? 10 : size,
                Sort.by(Sort.Direction.DESC, "lostDate")); // 분실일자(lostDate)를 기준으로 내림차순 정렬

        Page<LostItem> lostItemsList = lostItemRepository.findAll(pageable);

        return lostItemsList.map(LostItemListResponse::new); // DTO로 변환
    }

    // 분실물의 고유 ID로 분실물을 찾아 반환하는 메서드
    public LostItem findById(Long id) {

        return lostItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LostItem with ID " + id + " not found"));
    }

    // 이름을 기준으로 분실물 목록을 페이지 단위로 조회하는 메서드
    public Page<LostItemListResponse> findByTitle(String title, int page, int size) {

        Pageable pageable = PageRequest.of(page < 1 ? 0 : page - 1, size <= 0 ? 10 : size,
                Sort.by(Sort.Direction.DESC, "lostDate"));

        Page<LostItem> lostItems = lostItemRepository.findByTitle(title, pageable);

        return lostItems.map(LostItemListResponse::new);
    }

    // 분실물 삭제 메서드
    public void delete(Long id, String authHeader) {

        String accessToken = authHeader.replace("Bearer ", "");

        tokenService.validateToken(accessToken);

        Long userId = tokenService.getUserId(accessToken);
        LostItem lostItem = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LostItem with ID " + id + " not found."));

        // 삭제 요청한 사용자가 해당 분실물의 작성자와 일치하는지 확인
        if (!lostItem.getLoserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this lost item.");
        }

        lostItemRepository.deleteById(id);
    }


    // 분실물 수정 메서드
    @Transactional
    public LostItem update(long id, UpdateLostItemRequest request, String authHeader) {

        String accessToken = authHeader.replace("Bearer ", "");
        tokenService.validateToken(accessToken);

        Long userId = tokenService.getUserId(accessToken);
        LostItem lostItem = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LostItem with ID " + id + " not found."));

        // 수정 요청한 사용자가 해당 분실물의 작성자와 일치하는지 확인
        if (!lostItem.getLoserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this lost item.");
        }

        // 분실물 정보 업데이트
        lostItem.update(request.getTitle(), request.getDescription(), request.getLostLocation(), request.getLostDate(), request.getImageUrl(), request.getStatus());

        // DB에 반영
        return lostItemRepository.save(lostItem);
    }

//    public List<PostListResponse> findByUserId(Long userId) {
//        List<LostItem> items = lostItemRepository.findByLoserId(userId);
//        return items.stream()
//                .map(PostListResponse::new)
//                .toList();
//    }
}