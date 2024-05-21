package ahchacha.ahchacha.controller;


import ahchacha.ahchacha.domain.ChatMessage;
import ahchacha.ahchacha.domain.ChatRoom;
import ahchacha.ahchacha.repository.ChatMessageRepository;
import ahchacha.ahchacha.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody Map<String, Object> requestPayload) {
        String id = (String) requestPayload.get("itemId");

        Integer intId = Integer.parseInt(id);
        Long itemId = intId.longValue();
        return chatService.createRoom(itemId);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{itemId}")
    @ResponseBody
    public ChatRoom getRoom(@PathVariable Long itemId) {
        return chatService.findByStudyId(itemId);
    }

    // 채팅 내역
    @GetMapping("/history/{itemId}")
    public List<ChatMessage> getChatHistory(@PathVariable Long itemId) {
        return chatMessageRepository.findByItemId(itemId);
    }

}