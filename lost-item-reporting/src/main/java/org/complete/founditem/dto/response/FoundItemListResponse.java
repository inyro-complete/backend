package org.complete.founditem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FoundItemListResponse {
    private Long foundItemId;
    private String title;
    private String foundLocation;
    private LocalDateTime foundDate;
    private String status;
}
