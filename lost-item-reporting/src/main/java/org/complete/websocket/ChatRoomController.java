package org.complete.websocket;

import lombok.RequiredArgsConstructor;
import org.complete.domain.User;
import org.complete.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/room")
    public ResponseEntity<ChatRoomWithMessagesDto> makeRoom(@AuthenticationPrincipal User user,
                                                            @RequestBody ChatRoomRequestDto chatRoomRequestDto) {

        User other = userService.findById(chatRoomRequestDto.getOtherId());

        Optional<ChatRoom> optionalChatRoom = chatRoomService.findByUserAndOther(user, other);

        // user와 other 사이의 채팅방이 존재한다면 메시지들을 반환, 없으면 방을 생성
        if (optionalChatRoom.isPresent()) {
            ChatRoom foundChatRoom = optionalChatRoom.get();
            List<ChatMessageResponseDto> messages = chatMessageService.findMessages(foundChatRoom.getRoomNumber());
            return ResponseEntity.ok(new ChatRoomWithMessagesDto(foundChatRoom, messages));
        } else {
            ChatRoom newChatRoom = chatRoomService.createRoom(user, other);
            return ResponseEntity.ok(new ChatRoomWithMessagesDto(newChatRoom, new ArrayList<>()));
        }
    }

    // 사용자가 속한 채팅방 목록 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomWithMessagesDto>> findAll(@AuthenticationPrincipal User user) {
        List<ChatRoomWithMessagesDto> chatRooms = chatRoomService.findByUserId(user.getId());
        return ResponseEntity.ok(chatRooms);
    }

    //채팅방 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/rooms/{roomNumber}")
    public ResponseEntity<String> deleteRoom(@AuthenticationPrincipal User user, @PathVariable Long roomNumber) {
        chatRoomService.deleteRoom(user, roomNumber);
        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }
}