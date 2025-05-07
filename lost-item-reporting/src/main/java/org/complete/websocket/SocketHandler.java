package org.complete.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final AwsS3Service awsS3Service;

    // 방번호별 세션 관리
    private final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        JSONObject obj = jsonToObjectParser(payload);
        if (obj == null) return;

        String roomNumber = String.valueOf(obj.get("roomNumber"));
        String msgType = (String) obj.get("type");

        // 채팅 메시지 저장
        chatMessageService.save(ChatMessageRequestDto.builder()
                .userId((Long) obj.get("userId"))
                .message((String) obj.get("message"))
                .imageUrl(null)
                .roomNumber(Long.parseLong(roomNumber))
                .build());

        // 파일 업로드 메시지는 전송하지 않음
        if ("fileUpload".equals(msgType)) return;

        // 해당 방의 모든 세션에 메시지 전송
        Map<String, WebSocketSession> sessions = roomSessions.get(roomNumber);
        if (sessions != null) {
            TextMessage textMessage = new TextMessage(obj.toJSONString());
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                WebSocketSession wss = entry.getValue();
                if (wss != null && wss.isOpen()) {
                    try {
                        wss.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("메시지 전송 실패", e);
                    }
                }
            }
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteBuffer = message.getPayload();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        // 파일명 생성 및 S3에 업로드
        String fileName = "chatting" + UUID.randomUUID() + ".jpg";
        String contentType = "image/jpg";
        String dirName = "chatting_images";

        // AWS S3에 파일 업로드
        ByteArrayMultipartFile multipartFile = new ByteArrayMultipartFile(bytes, contentType, fileName);
        URL uploadedFileUrl = awsS3Service.upload(multipartFile, dirName);

        byteBuffer.position(0);

        Long roomNumber = (Long) session.getAttributes().get("roomNumber");
        Long userId = (Long) session.getAttributes().get("userId");

        chatMessageService.save(ChatMessageRequestDto.builder()
                .userId(userId)
                .message("image")
                .imageUrl(uploadedFileUrl.toString())
                .roomNumber(roomNumber)
                .build());

        String roomKey = String.valueOf(roomNumber);
        Map<String, WebSocketSession> sessions = roomSessions.get(roomKey);
        if (sessions != null) {
            for (WebSocketSession wss : sessions.values()) {
                if (wss != null && wss.isOpen()) {
                    try {
                        wss.sendMessage(new BinaryMessage(byteBuffer));
                    } catch (IOException e) {
                        log.error("이미지 메시지 전송 실패", e);
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String url = session.getUri().toString();
        String roomNumber = url.split("/chat/")[1];

        // 해당 방의 세션 목록 가져오기 또는 생성
        roomSessions.computeIfAbsent(roomNumber, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);

        // 세션 ID를 클라이언트에 전달
        JSONObject obj = new JSONObject();
        obj.put("type", "getId");
        obj.put("sessionId", session.getId());
        session.sendMessage(new TextMessage(obj.toJSONString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("세션 종료: {}", session.getId());
        // 세션이 종료될 때 해당 방에서 세션을 제거
        roomSessions.values().forEach(room -> room.remove(session.getId()));
        super.afterConnectionClosed(session, status);
    }

    private static JSONObject jsonToObjectParser(String jsonStr) {
        try {
            return (JSONObject) new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            log.error("JSON 파싱 실패", e);
            return null;
        }
    }
}
