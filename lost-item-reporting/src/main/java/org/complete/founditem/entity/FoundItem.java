package org.complete.founditem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "found_item_id", updatable = false)
    private Long id;

    /** 습득물 제목 (예: "지갑", "휴대폰") */
    @Column(nullable = false, length = 255)
    private String title;

    /** 상세 설명 (예: "7016버스 뒷좌석에서 발견된 검정색 지갑") */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 발견 장소 (예: "7016버스", "2호선 강남역") */
    @Column(nullable = false, length = 255)
    private String foundLocation;

    /** 발견 일시 (예: "2025-03-23T14:30:00") */
    @Column(nullable = false)
    private LocalDateTime foundDate;

    /** 보관 장소 (예: "서울 분실물 센터") */
    @Column(nullable = false, length = 255)
    private String storageLocation;

    /** 보관 연락처 (예: "02-123-4567") - 선택 입력 */
    @Column
    private String storageContact;

    /** 분실자 이름 (예: "홍길동") - 선택 입력 */
    @Column
    private String loserName;

    /** 이미지 URL (예: "https://example.com/images/item.jpg") - 선택 입력 */
    @Column
    private String imageUrl;

    /**
     * 상태:
     * FOUND    - 보관 중
     * RETURNED - 반환 완료
     * DISPOSED - 폐기됨
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FoundItemStatus status;

    /** 등록자(사용자) ID (예: 1) - 로그인한 사용자 ID로 자동 매핑됨 */
    @Column(nullable = false)
    private Long finderId;

    public enum FoundItemStatus {
        /** 보관 중인 상태 */
        FOUND,

        /** 주인에게 반환된 상태 */
        RETURNED,

        /** 처리되어 폐기된 상태 */
        DISPOSED
    }

    public void update(String title, String description, String foundLocation, LocalDateTime foundDate,
                       String storageLocation, String storageContact, String loserName, String imageUrl,
                       FoundItemStatus status) {
        this.title = title;
        this.description = description;
        this.foundLocation = foundLocation;
        this.foundDate = foundDate;
        this.storageLocation = storageLocation;
        this.storageContact = storageContact;
        this.loserName = loserName;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}
