package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.dto.NotificationDto;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(notificationRequestDto);
    }
}
