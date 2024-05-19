package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.common.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    @Getter
    @Builder
    public static class NotificationResponseDto{
        private Long id;
        private String writer;
        private Long commentId;
        private String comment;
        private Long communityId;
        private String communityTitle;
        private String itemTitle;
        private boolean isRead;
        private LocalDateTime createdAt;
        private NotificationType notificationType;
    }
}
