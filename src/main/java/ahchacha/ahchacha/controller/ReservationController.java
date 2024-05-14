package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.dto.ReservationDto;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("reservation")
public class ReservationController {

    private ReservationService reservationService;
    private ReservationRepository reservationRepository;

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

    @Operation(summary = "내가 등록한 모든 item들의 예약 조회(마이페이지-등록내역)")
    @GetMapping("/myAllItemsRentedByOther")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getMyAllItemsRentedByOther(HttpServletRequest request,
                                                                                               @RequestParam(value = "page", defaultValue = "1") int page) {
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getMyAllItemsRentedByOther(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 등록한 item들의 예약완료 조회(마이페이지-등록내역)")
    @GetMapping("/myItemsReservedByOther")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getMyItemsReservedByOther(HttpServletRequest request,
                                                                                               @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getMyItemsReservedByOther(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 등록한 item들의 대여중 조회(마이페이지-등록내역)")
    @GetMapping("/myItemsRentingByOther")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getMyItemsRentingByOther(HttpServletRequest request,
                                                                                                 @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getMyItemsRentingByOther(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "내가 등록한 item들의 반납완료 조회(마이페이지-등록내역)")
    @GetMapping("/myItemsReturnedByOther")
    public ResponseEntity<Page<ReservationDto.ReservationResponseDto>> getMyItemsReturnedByOther(HttpServletRequest request,
                                                                                                @RequestParam(value = "page", defaultValue = "1") int page) {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        Page<ReservationDto.ReservationResponseDto> myItems = reservationService.getMyItemsReturnedByOther(page, currentUser);

        return ResponseEntity.ok(myItems);
    }

    @Operation(summary = "예약완료 아이템 대여중으로 변경", description = "대여처리 시 rentingStatus: RESERVED -> RENTING")
    @PatchMapping("/{reservationId}/updateReservedToRenting")
    public ResponseEntity<String> updateReservedToRentingStatusForItem(@PathVariable Long reservationId, HttpSession session) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + reservationId));

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        if (!reservation.getItemUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to update the renting status of this item.");
        }

        // updateRentingStatusForItem 메서드를 호출하여 아이템의 대여 상태를 업데이트합니다.
        reservationService.updateReservedToRentingStatusForReservation(reservation);

        return ResponseEntity.ok("Renting status updated successfully.");
    }


    @Operation(summary = "대여중 아이템 반납완료로 변경", description = "반납처리 시 RENTINGSTATUS: RENTING -> RETURNED")
    @PatchMapping("/{reservationId}/updateRentingToReturned")
    public ResponseEntity<String> updateRentingToReturnedStatusForReservation(@PathVariable Long reservationId, HttpSession session) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + reservationId));

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        if (!reservation.getItemUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to update the renting status of this item.");
        }

        // updateRentingStatusForItem 메서드를 호출하여 아이템의 대여 상태를 업데이트합니다.
        reservationService.updateRentingToReturnedStatusForReservation(reservation);

        return ResponseEntity.ok("Renting status updated successfully.");
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
