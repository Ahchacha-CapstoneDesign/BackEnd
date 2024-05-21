package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.ChatMessage;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.repository.ChatMessageRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    public ChatMessage saveChatMessage(ChatMessage chatMessage, HttpSession session) {
        User user = (User) session.getAttribute("user");

        chatMessage.setUser(user);

        return chatMessageRepository.save(chatMessage);
    }

}