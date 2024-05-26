package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.*;
import ahchacha.ahchacha.dto.ReservationDto;
import ahchacha.ahchacha.repository.ItemRepository;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class ReservationService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ReservationRepository reservationRepository;
    private NotificationRepository notificationRepository;

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getAllItemsInMyPage(int page, User user) { //내가 대여한 모든 아이템
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        return reservationRepository.findByUser(user, pageable)
                .map(ReservationDto.ReservationResponseDto::toDto);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getAllItemsByReserved(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.findByUserAndRentingStatus(user, RentingStatus.RESERVED, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getAllItemsByRenting(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.findByUserAndRentingStatus(user, RentingStatus.RENTING, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getAllItemsByReturned(int page, User user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.findByUserAndRentingStatus(user, RentingStatus.RETURNED, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }


    ///////////////////////////////////////////////////////////////////////////////
    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getMyAllItemsRentedByOther(int page, User currentUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.findByItemUserId(currentUser.getId(), pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getMyItemsReservedByOther(int page, User currentUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.
                findByItemUserIdAndRentingStatus(currentUser.getId(), RentingStatus.RESERVED, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getMyItemsRentingByOther(int page, User currentUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.
                findByItemUserIdAndRentingStatus(currentUser.getId(), RentingStatus.RENTING, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    @Transactional
    public Page<ReservationDto.ReservationResponseDto> getMyItemsReturnedByOther(int page, User currentUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt")); // 최근 작성순

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        Page<Reservations> itemPage = reservationRepository.
                findByItemUserIdAndRentingStatus(currentUser.getId(), RentingStatus.RETURNED, pageable);
        return ReservationDto.toDtoPage(itemPage);
    }

    //예약완료 -> 대여중 (등록한 사람이 하는 것)
    @Transactional
    public void updateReservedToRentingStatusForReservation(Reservations reservations) {
        if (reservations.getRentingStatus() == RentingStatus.RESERVED) {
            reservations.setRentingStatus(RentingStatus.RENTING);
            reservationRepository.save(reservations);

            Item item = reservations.getItem();
            if (item != null) {
                    item.setRentingStatus(RentingStatus.RENTING);
                    itemRepository.save(item);
                }
            }
    }

    //대여중 -> 반납완료 (등록한 사람이 하는 것)
    @Transactional
    public void updateRentingToReturnedStatusForReservation(Reservations reservations) {
        if (reservations.getRentingStatus() == RentingStatus.RENTING) {
            reservations.setRentingStatus(RentingStatus.RETURNED);
            reservationRepository.save(reservations);

            Item item = reservations.getItem();
            if (item != null) {
                item.setRentingStatus(RentingStatus.RETURNED);
                itemRepository.save(item);
            }
        }
    }


    //개인이 올린 item 예약
    public void createPersonReservation(ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Item item = itemRepository.findById(reservationDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        long minutesBetween = ChronoUnit.MINUTES.between(reservationDTO.getBorrowTime(), reservationDTO.getReturnTime());
        double hoursBetween = minutesBetween / 60.0; // 분을 시간으로 변환
        int totalPrice = (int) (hoursBetween * item.getPricePerHour());

        String itemRegisterDefaultProfile = item.getUser().getDefaultProfile(); //아이템 등록한사람의 프로필
        String imageUrl = null;
        List<String> imageUrls = item.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrl = imageUrls.get(0);
        } // 첫번쨰 이미지 추출

        Reservations reservation = Reservations.builder()
                .title(item.getTitle())
                .rentingStatus(item.getRentingStatus())
                .rentingStatus(RentingStatus.RESERVED)
                .borrowTime(reservationDTO.getBorrowTime())
                .returnTime(reservationDTO.getReturnTime())
                .totalPrice(totalPrice)
                .item(item)
//                .itemUserNickName(item.getUser().getNickname())
                .itemBorrowPlace(item.getBorrowPlace())
                .itemReturnPlace(item.getReturnPlace())
//                .userNickname(user.getNickname())
                .userPhoneNumber(user.getPhoneNumber())
                .cancelStatus(false)
//                .itemRegisterDefaultProfile(itemRegisterDefaultProfile)
//                .userDefaultProfile(user.getDefaultProfile()) // 예약하는 사람의 프로필
//                .imageUrls(Collections.singletonList(imageUrl))
                .imageUrls(imageUrl != null ? Collections.singletonList(imageUrl) : null)

                .user(user) //대여하는 사람의 학번
                .itemUserId(item.getUser().getId()) //아이템 주인의 학번(id)

                .toRenterWrittenStatus(ToRenterWrittenStatus.NONWRITTEN) //리뷰 안쓴상태
                .toOwnerWrittenStatus(ToOwnerWrittenStatus.NONWRITTEN)

                .build();

        item.setReservation(Reservation.NO); // 예약 가능 상태를 NO로 설정 = 예약불가
        item.setRentingStatus(RentingStatus.RESERVED); // 예약완료
        reservationRepository.save(reservation);

        sendNotification(user, reservation);
        sendNotification(item.getUser(),reservation);
    }

    //official이 올린 item 예약
    public void createOfficialReservation(ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Item item = itemRepository.findById(reservationDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        String itemRegisterDefaultProfile = item.getUser().getDefaultProfile(); //아이템 등록한사람의 프로필
        String imageUrl = null;
        List<String> imageUrls = item.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrl = imageUrls.get(0);
        } // 첫번쨰 이미지 추출

        Reservations reservation = Reservations.builder()
                .title(item.getTitle())
                .rentingStatus(item.getRentingStatus())
                .rentingStatus(RentingStatus.RESERVED)
                .borrowTime(reservationDTO.getBorrowTime())
                .returnTime(reservationDTO.getReturnTime())
                .item(item)
//                .itemUserNickName(item.getUser().getNickname())
                .itemBorrowPlace(item.getBorrowPlace())
                .itemReturnPlace(item.getReturnPlace())
                .user(user) //학번
                .userName(user.getName()) //이름
                .userPhoneNumber(user.getPhoneNumber())
                .userTrack1(user.getTrack1()) //트랙
                .userGrade(user.getGrade()) //4학년
                .userStatus(user.getStatus()) //복학
                .cancelStatus(false)
//                .itemRegisterDefaultProfile(itemRegisterDefaultProfile)
//                .userDefaultProfile(user.getDefaultProfile()) // 예약하는 사람의 프로필
//                .imageUrls(Collections.singletonList(imageUrl))
                .imageUrls(imageUrl != null ? Collections.singletonList(imageUrl) : null)
                .itemUserId(item.getUser().getId()) //아이템 주인의 학번(id)

                .toRenterWrittenStatus(ToRenterWrittenStatus.NONWRITTEN) //리뷰 안쓴상태
                .toOwnerWrittenStatus(ToOwnerWrittenStatus.NONWRITTEN)

                .build();

        item.setReservation(Reservation.NO); // 예약 가능 상태를 NO로 설정
        item.setRentingStatus(RentingStatus.RESERVED); // 예약완료
        reservationRepository.save(reservation);

        sendNotification(user, reservation);
        sendNotification(item.getUser(),reservation);
    }

    @Transactional
    public void deleteReservationAndResetRentingStatusByRenter(Long reservationId, User currentUser) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this reservation.");
        }

        Item item = reservation.getItem();
        item.setRentingStatus(RentingStatus.NONE);
        item.setReservation(Reservation.YES);

        reservationRepository.delete(reservation);
    }

    @Transactional
    public void deleteReservationAndResetRentingStatusByOwner(Long reservationId, User currentUser) {
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        if (!reservation.getItemUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this reservation.");
        }

        Item item = reservation.getItem();
        item.setRentingStatus(RentingStatus.NONE);
        item.setReservation(Reservation.YES);

        reservationRepository.delete(reservation);
    }

    // 대여한 사람이 예약 취소
    public void cancelReservationByRenter(Long reservationId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // 예약을 한 사용자와 현재 세션의 사용자가 일치하는지 확인
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only cancel your own reservations");
        }

        // 아이템 상태를 초기화
        Item item = reservation.getItem();
        item.setReservation(Reservation.YES); // 예약 가능
        item.setRentingStatus(RentingStatus.NONE);
        itemRepository.save(item);

        // 알림 전송
        sendCancelNotification(user, reservation);
        sendCancelNotification(item.getUser(), reservation);

        reservation.setCancelStatus(true);
        reservationRepository.save(reservation);

        // 예약 내역 삭제
//        reservationRepository.delete(reservation);
    }

    //아이템 주인이 예약 취소
    public void cancelReservationByItemOwner(Long reservationId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        //아이템 주인과 현재 세션의 사용자가 일치하는지 확인
        if (!reservation.getItem().getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only cancel your own reservations");
        }

        // 아이템 상태를 초기화
        Item item = reservation.getItem();
        item.setReservation(Reservation.YES); // 예약 가능
        item.setRentingStatus(RentingStatus.NONE);
        itemRepository.save(item);

        // 알림 전송
        sendCancelNotification(user, reservation);
        sendCancelNotification(item.getUser(), reservation);

        reservation.setCancelStatus(true);
        reservationRepository.save(reservation);

        // 예약 내역 삭제
//        reservationRepository.delete(reservation);
    }

    private void sendNotification(User user, Reservations reservations) {
        Notification notification = Notification.builder()
                .user(user)
                .reservations(reservations)
                .notificationType(NotificationType.RESERVATION)
                .isRead(false)  // 초기에 알림은 읽지 않음
                .build();

        notificationRepository.save(notification);
    }

    private void sendCancelNotification(User user, Reservations reservations) {
        Notification notification = Notification.builder()
                .user(user)
                .reservations(reservations)
                .notificationType(NotificationType.CANCEL)
                .isRead(false)  // 초기에 알림은 읽지 않음
                .build();

        notificationRepository.save(notification);
    }
}
