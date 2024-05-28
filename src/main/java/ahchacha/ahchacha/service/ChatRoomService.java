package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.ChatRoom;
import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.repository.ChatRoomRepository;
import ahchacha.ahchacha.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 하나 불러오기
    public ChatRoom findByStudyId(Long itemId) {
        return chatRoomRepository.findByItemId(itemId);
    }

    //채팅방 생성
    @Transactional
    public ChatRoom createRoom(Long itemId, HttpSession session) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));

        User user = (User) session.getAttribute("user");

        ChatRoom chatRoom = ChatRoom.builder()
                .item(item)
                .user(user)
                .build();

        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}