package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.ChatMessage;
import ahchacha.ahchacha.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByItemId(Long itemId);

    List<ChatMessage> findByUser(User user);
}
