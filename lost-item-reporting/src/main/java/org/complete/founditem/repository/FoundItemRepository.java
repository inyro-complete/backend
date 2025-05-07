package org.complete.founditem.repository;

import org.complete.founditem.entity.FoundItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {

    /**
     * 제목에 특정 키워드가 포함된 습득물 목록을 페이징하여 조회
     *
     * @param title    검색할 제목 키워드 (예: "지갑")
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 등)
     * @return 제목에 해당 키워드가 포함된 FoundItem 페이지 결과
     */
    Page<FoundItem> findByTitleContaining(String title, Pageable pageable);
}
