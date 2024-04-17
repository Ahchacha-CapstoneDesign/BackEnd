package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.NotificationDto;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationDto.NotificationResponseDto> getNotificationByUser(HttpSession session) {
        User user = (User) session.getAttribute("user");

        List<Notification> notifications = notificationRepository.findByUser(user);

        List<NotificationDto.NotificationResponseDto> responseDtos = notifications.stream()
                .map(notification -> {
                    NotificationDto.NotificationResponseDto.NotificationResponseDtoBuilder builder =
                            NotificationDto.NotificationResponseDto.builder()
                                    .id(notification.getId())
                                    .isRead(notification.isRead());

                    if (notification.getComment() != null) {
                        // 댓글 알림인 경우
                        builder.writer(notification.getComment().getUser().getNickname())
                                .commentId(notification.getComment().getId())
                                .comment(notification.getComment().getContent())
                                .communityId(notification.getComment().getCommunity().getId())
                                .communityTitle(null)
                                .createdAt(notification.getCreatedAt());
                    } else if (notification.getHeart() != null) {
                        // 좋아요 알림인 경우
                        builder.writer(notification.getHeart().getUser().getNickname())
                                .commentId(null)
                                .comment(null)
                                .communityId(notification.getHeart().getCommunity().getId())
                                .communityTitle(notification.getHeart().getCommunity().getTitle())
                                .createdAt(notification.getCreatedAt());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());

        // 최신 순으로 정렬
        responseDtos.sort(Comparator.comparing(NotificationDto.NotificationResponseDto::getCreatedAt).reversed());

        return responseDtos;
    }
}
