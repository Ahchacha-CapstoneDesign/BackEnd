package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.NotificationType;
import ahchacha.ahchacha.dto.NotificationDto;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public List<NotificationDto.NotificationResponseDto> getNotificationByUser(HttpSession session) {
        User user = (User) session.getAttribute("user");

        List<Notification> notifications = notificationRepository.findByUser(user);

        List<NotificationDto.NotificationResponseDto> responseDtos = notifications.stream()
                .map(notification -> {
                    NotificationDto.NotificationResponseDto.NotificationResponseDtoBuilder builder =
                            NotificationDto.NotificationResponseDto.builder()
                                    .id(notification.getId())
                                    .notificationType(notification.getNotificationType())
                                    .isRead(notification.isRead());

                    if (notification.getComment() != null) {
                        // 댓글 알림인 경우
                        builder.writer(notification.getComment().getUser().getNickname())
                                .commentId(notification.getComment().getId())
                                .comment(notification.getComment().getContent())
                                .communityId(notification.getComment().getCommunity().getId())
                                .communityTitle(null)
                                .itemTitle(null)
                                .createdAt(notification.getCreatedAt());
                    } else if (notification.getHeart() != null) {
                        // 좋아요 알림인 경우
                        builder.writer(notification.getHeart().getUser().getNickname())
                                .commentId(null)
                                .comment(null)
                                .communityId(notification.getHeart().getCommunity().getId())
                                .communityTitle(notification.getHeart().getCommunity().getTitle())
                                .itemTitle(null)
                                .createdAt(notification.getCreatedAt());
                    } else if (notification.getReservations() != null) {
                        // 예약 완료 또는 반납 시간 알림인 경우
                        builder.writer(notification.getReservations().getItem().getUser().getNickname())
                                .commentId(null)
                                .comment(null)
                                .communityId(null)
                                .communityTitle(null)
                                .itemTitle(notification.getReservations().getItem().getTitle())
                                .createdAt(notification.getCreatedAt());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());

        // 최신 순으로 정렬
        responseDtos.sort(Comparator.comparing(NotificationDto.NotificationResponseDto::getCreatedAt).reversed());

        return responseDtos;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void sendReturnNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusMinutes(60);
        LocalDateTime twentyFourHoursLater = now.plusHours(24);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            // 한 시간 전 예약 정보 조회
            List<Reservations> oneHourReservations = reservationRepository.findByUserAndReturnTimeBetween(user, now, oneHourLater);
            // 24시간 전 예약 정보 조회
            List<Reservations> twentyFourHourReservations = reservationRepository.findByUserAndReturnTimeBetween(user, now, twentyFourHoursLater);

            // 한 시간 전 예약에 대한 알림 생성
            for (Reservations reservation : oneHourReservations) {
                if(reservation.isNotificationSentHour()) continue;
                Notification notification = Notification.builder()
                        .user(user)
                        .reservations(reservation)
                        .notificationType(NotificationType.RETURN_ONE_HOUR)
                        .isRead(false)
                        .build();
                reservation.setNotificationSentHour(true);
                reservationRepository.save(reservation);
                notificationRepository.save(notification);
            }
            // 24시간 전 예약에 대한 알림 생성
            for (Reservations reservation : twentyFourHourReservations) {
                if(reservation.isNotificationSentDay()) continue;
                Notification notification = Notification.builder()
                        .user(user)
                        .reservations(reservation)
                        .notificationType(NotificationType.RETURN_ONE_DAY)
                        .isRead(false)
                        .build();
                reservation.setNotificationSentDay(true);
                reservationRepository.save(reservation);
                notificationRepository.save(notification);
            }
        }
    }
}
