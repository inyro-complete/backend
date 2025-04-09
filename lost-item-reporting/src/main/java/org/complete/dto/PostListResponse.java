//package org.complete.dto;
//
//import lombok.Getter;
//import org.complete.domain.LostItem;
//
//import java.time.LocalDateTime;
//
//@Getter
//public class PostListResponse {
//    private final Long id;
//    private final String title;
//    private final String lostLocation;
//    private final LocalDateTime lostDate;
//    private final String imageUrl;
//    private final String type;
//
//    public PostListResponse(LostItem lostItem) {
//        this.id = lostItem.getLostItemId();
//        this.title = lostItem.getTitle();
//        this.lostLocation = lostItem.getLostLocation();
//        this.lostDate = lostItem.getLostDate();
//        this.imageUrl = lostItem.getImageUrl();
//        this.type = "lost";
//    }
//
//    public PostListResponse(FoundItem foundItem) {
//        this.id = foundItem.getFoundItemId();
//        this.title = foundItem.getTitle();
//        this.lostLocation = foundItem.getFoundLocation();
//        this.lostDate = foundItem.getFoundDate();
//        this.imageUrl = foundItem.getImageUrl();
//        this.type = "found";
//    }
//}
