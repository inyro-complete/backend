package org.complete.repository;

import org.complete.domain.LostItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    Page<LostItem> findByTitle(String title, Pageable pageable);
    List<LostItem> findByLoserId(Long loserId);
}