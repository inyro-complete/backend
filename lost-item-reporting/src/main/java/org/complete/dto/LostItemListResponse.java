package org.complete.dto;

import lombok.Getter;
import org.complete.domain.LostItem;

import java.time.LocalDateTime;

@Getter
public class LostItemListResponse {

    private final Long lostItemId;
    private final String title;
    private final String lostLocation;
    private final LocalDateTime lostDate;
    private final String status;

    public LostItemListResponse(LostItem lostItem) {
        this.lostItemId = lostItem.getLostItemId();
        this.title = lostItem.getTitle();
        this.lostLocation = lostItem.getLostLocation();
        this.lostDate = lostItem.getLostDate();
        this.status = lostItem.getStatus();
    }
}