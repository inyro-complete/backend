package org.complete.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SocketHandler implements WebSocketHandler {

    private final Map<Long, Map<Long, WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Map<String, Object> attributes = session.getAttributes();
            Long roomNumber = (Long) attributes.get("roomNumber");
            Long userId = (Long) attributes.get("userId");

            if (roomNumber == null || userId == null) {
                log.error("roomNumber 또는 userId가 null입니다. 연결 종료");
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            chatRooms
                    .computeIfAbsent(roomNumber, k -> new ConcurrentHashMap<>())
                    .put(userId, session);

            log.info("사용자 {}가 방 {}에 연결됨", userId, roomNumber);

        } catch (Exception e) {
            log.error("afterConnectionEstablished 오류", e);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Exception ignore) {}
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.info("수신된 메시지: {}", message.getPayload());
        // 메시지 처리 로직 (예: 브로드캐스트 등)
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("전송 중 오류 발생", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Map<String, Object> attributes = session.getAttributes();
        Long roomNumber = (Long) attributes.get("roomNumber");
        Long userId = (Long) attributes.get("userId");

        if (roomNumber != null && userId != null) {
            Map<Long, WebSocketSession> room = chatRooms.get(roomNumber);
            if (room != null) {
                room.remove(userId);
                if (room.isEmpty()) {
                    chatRooms.remove(roomNumber);
                }
            }
        }

        log.info("사용자 {}가 방 {}에서 연결 종료", userId, roomNumber);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
