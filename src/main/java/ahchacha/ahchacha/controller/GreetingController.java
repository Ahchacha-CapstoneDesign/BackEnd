package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.ChatMessage;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.repository.UserRepository;
import ahchacha.ahchacha.service.ChatMessageService;
import ahchacha.ahchacha.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;


@RestController
@RequiredArgsConstructor
public class GreetingController {
    final ChatMessageService chatMessageService;
    final UserService userService;
    final UserRepository userRepository;

    // 채팅
    @MessageMapping("/chat/{itemId}")
    @SendTo("/queue/greetings/{itemId}")
    public ChatMessage chat(ChatMessage message, HttpSession session){
        User user = (User) session.getAttribute("user");

        // 채팅 메시지 저장
        ChatMessage savedChat = chatMessageService.saveChatMessage(message, session);

        return savedChat;
    }
}
