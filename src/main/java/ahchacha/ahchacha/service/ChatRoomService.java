package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.ChatRoom;
import ahchacha.ahchacha.repository.ChatRoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
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
//    public ChatRoom createRoom(Long itemId) {
//        ChatRoom chatRoom = new ChatRoom(itemId);
//        chatRoomRepository.save(chatRoom);
//        return chatRoom;
//    }
}