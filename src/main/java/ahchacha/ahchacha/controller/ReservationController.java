package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.dto.ReservationDto;
import ahchacha.ahchacha.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "내가 대여한 모든 item들 조회(마이페이지-대여내역)")
    @GetMapping("/myItems")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getAllItemsInMyPage(HttpServletRequest request,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getAllItemsInMyPage(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 대여한 모든 예약완료 item들 조회(마이페이지-대여내역-예약완료)")
    @GetMapping("/itemsRESERVED")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getAllItemsByReserved(HttpServletRequest request,
                                                                                           @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getAllItemsByReserved(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 대여한 모든 대여중 item들 조회(마이페이지-대여내역-대여중)")
    @GetMapping("/itemsRENTING")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getAllItemsByRenting(HttpServletRequest request,
                                                                              @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getAllItemsByRenting(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 대여한 모든 반납완료 item들 조회(마이페이지-대여내역-반납완료)")
    @GetMapping("/itemsRENTED")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getAllItemsByReturned(HttpServletRequest request,
                                                                                            @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getAllItemsByReturned(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "예약 삭제 by 아이템 빌린 사람", description = "reservation의 id를 입력하세요")
    @DeleteMapping("/renter/{reservationId}")
    public ResponseEntity<?> deleteReservationAndResetRentingStatusByRenter(@PathVariable Long reservationId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        reservationService.deleteReservationAndResetRentingStatusByRenter(reservationId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약 삭제 by 아이템 주인", description = "reservation의 id를 입력하세요")
    @DeleteMapping("/owner/{reservationId}")
    public ResponseEntity<?> deleteReservationAndResetRentingStatusByOwner(@PathVariable Long reservationId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        reservationService.deleteReservationAndResetRentingStatusByOwner(reservationId, user);
        return ResponseEntity.ok().build();
    }
}
