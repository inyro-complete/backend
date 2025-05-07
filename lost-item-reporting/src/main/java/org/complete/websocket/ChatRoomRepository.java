package org.complete.websocket;

import org.complete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserAndOther(User user, User other);
    List<ChatRoom> findByUserId(Long userId);
    Optional<ChatRoom> findByRoomNumber(Long roomNumber);
}
