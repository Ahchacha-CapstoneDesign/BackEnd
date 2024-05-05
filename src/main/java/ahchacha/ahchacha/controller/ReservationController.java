package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.dto.ReservationDto;
import ahchacha.ahchacha.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reservation")
public class ReservationController {


    @Autowired
    private ReservationService reservationService;

    @Operation(summary = "person이 올린 item 예약", description = "cBorrowDateTime/returnTime 예시 : 2024-03-17T10:30:00")
    @PostMapping("/person")
    public ResponseEntity<?> createPersonReservation(@RequestBody ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        reservationService.createPersonReservation(reservationDTO, session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "official이 올린 item 예약", description = "cBorrowDateTime/returnTime 예시 : 2024-03-17T10:30:00")
    @PostMapping("/official")
    public ResponseEntity<?> createOfficialReservation(@RequestBody ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        reservationService.createOfficialReservation(reservationDTO, session);
        return ResponseEntity.ok().build();
    }
}
