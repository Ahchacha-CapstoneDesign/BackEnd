package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByItemId(Long itemId);
}