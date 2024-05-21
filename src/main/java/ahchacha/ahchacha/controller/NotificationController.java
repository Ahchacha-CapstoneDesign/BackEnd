package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.dto.NotificationDto;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 조회")
    @GetMapping
    public ResponseEntity<List<NotificationDto.NotificationResponseDto>> getAllNotifications(HttpSession session) {
        List<NotificationDto.NotificationResponseDto> notificationRequestDto = notificationService.getNotificationByUser(session);
        return new ResponseEntity<>(notificationRequestDto, HttpStatus.OK);
    }

    @Operation(summary = "알림 읽음 상태 업데이트")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
