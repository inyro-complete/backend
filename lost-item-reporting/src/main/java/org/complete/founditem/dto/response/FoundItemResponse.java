package org.complete.founditem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoundItemResponse {
    private Long foundItemId;
    private String title;
    private String description;
    private String foundLocation;
    private String imageUrl;
    private String status;
}
