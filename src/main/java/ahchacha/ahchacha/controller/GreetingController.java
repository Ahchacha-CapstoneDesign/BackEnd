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
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.HtmlUtils;


@RestController
@RequiredArgsConstructor
public class GreetingController {
    final ChatMessageService chatMessageService;
    final UserService userService;
    final UserRepository userRepository;

//    public Authentication getCurrentUserAuthentication() {
//        return SecurityContextHolder.getContext().getAuthentication();
//    }

    // 입장
    @MessageMapping("/enter/{itemId}")
    @SendTo("/topic/greetings/{itemId}")
    public ChatMessage enter(ChatMessage message, HttpSession session) {
//        Authentication authentication = getCurrentUserAuthentication();
        User user = (User) session.getAttribute("user");
        return new ChatMessage(HtmlUtils.htmlEscape(user.getNickname() + "님이 입장하였습니다."));
    }

    // 퇴장
    @MessageMapping("/exit/{itemId}")
    @SendTo("/topic/greetings/{itemId}")
    public ChatMessage exit(ChatMessage message, HttpSession session) throws Exception {
//        Authentication authentication = getCurrentUserAuthentication();
        User user = (User) session.getAttribute("user");
        return new ChatMessage(HtmlUtils.htmlEscape(user.getNickname() + "님이 퇴장하였습니다."));
    }

    // 채팅
    @MessageMapping("/chat/{itemId}")
    @SendTo("/topic/greetings/{itemId}")
    public ChatMessage chat(ChatMessage message, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");

        // 채팅 메시지 저장
        ChatMessage savedChat = chatMessageService.saveChatMessage(message, session);

        return savedChat;
    }
}
