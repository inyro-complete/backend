package org.complete.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {

    private Long userId;
    private Long otherId;
}
