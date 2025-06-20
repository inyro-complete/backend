package org.complete.websocket;

import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    //채팅방 개설
    @Transactional
    public ChatRoom createRoom(User user, User other) {
        return chatRoomRepository.save(ChatRoom.builder()
                .user(user)
                .other(other)
                .build());
    }

    @Transactional
    public Optional<ChatRoom> findByUserAndOther(User user, User other) {
        return chatRoomRepository.findByUserAndOther(user, other);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomWithMessagesDto> findByUserId(Long userId) {

        List<ChatRoom> rooms = chatRoomRepository.findByUserId(userId);

        return rooms.stream()
                .map(room -> {
                    List<ChatMessageResponseDto> messages = chatMessageService.findMessages(room.getRoomNumber());
                    return new ChatRoomWithMessagesDto(room, messages);
                })
                .collect(Collectors.toList());
    }

    //채팅방 삭제
    @Transactional
    public void deleteRoom(User user, Long roomNumber) {
        // 채팅방을 찾기
        ChatRoom chatRoom = chatRoomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));

        // 사용자가 해당 채팅방에 참여했는지 확인
        if (!chatRoom.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 채팅방을 삭제할 권한이 없습니다.");
        }

        // 해당 채팅방에 속한 메시지들 삭제
        chatMessageService.deleteByRoomNumber(roomNumber);

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }
}
