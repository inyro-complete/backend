package org.complete.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동으로 1씩 증가
    @Column(name = "lost_item_id", updatable = false) // DB의 id 컬럼과 매핑, id는 수정 불가
    private Long lostItemId;

    @Column(name = "title", nullable = false) // NULL 값 허용하지 않음
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "lost_location", nullable = false)
    private String lostLocation;

    @Column(name = "lost_date", nullable = false)
    private LocalDateTime lostDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status", nullable = false)
    private String status;

    @JoinColumn(name = "loser_id", referencedColumnName = "id", nullable = false)
    private Long loserId;

    @Builder
    public LostItem(String title, String description, String lostLocation, LocalDateTime lostDate,
                    String imageUrl, String status, Long loserId) {
        this.title = title;
        this.description = description;
        this.lostLocation = lostLocation;
        this.lostDate = lostDate;
        this.imageUrl = imageUrl;
        this.status = status;
        this.loserId = loserId;
    }

    public void update(String title, String description, String lostLocation,
                       LocalDateTime lostDate, String imageUrl, String status) {
        this.title = title;
        this.description = description;
        this.lostLocation = lostLocation;
        this.lostDate = lostDate;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}