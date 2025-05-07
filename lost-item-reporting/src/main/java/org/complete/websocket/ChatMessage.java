package org.complete.websocket;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String message;
    private Long roomNumber;
    private String imageUrl;
    private LocalDateTime createdTime;

    @Builder
    public ChatMessage(Long userId, String message, String imageUrl, Long roomNumber) {
        this.userId = userId;
        this.message = message;
        this.roomNumber = roomNumber;
        this.imageUrl = imageUrl;
        this.createdTime = LocalDateTime.now();
    }
}
