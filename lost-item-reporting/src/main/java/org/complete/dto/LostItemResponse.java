package org.complete.dto;

import lombok.Getter;
import org.complete.domain.LostItem;

import java.time.LocalDateTime;

@Getter
public class LostItemResponse {

    private final Long lostItemId;
    private final String title;
    private final String description;
    private final String lostLocation;
    private final LocalDateTime lostDate;
    private final String imageUrl;
    private final String status;

    public LostItemResponse(LostItem lostItem) {
        this.lostItemId = lostItem.getLostItemId();
        this.title = lostItem.getTitle();
        this.description = lostItem.getDescription();
        this.lostLocation = lostItem.getLostLocation();
        this.lostDate = lostItem.getLostDate();
        this.imageUrl = lostItem.getImageUrl();
        this.status = lostItem.getStatus();
    }
}
