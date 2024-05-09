package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.dto.ReservationDto;
import ahchacha.ahchacha.repository.ItemRepository;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    //개인이 올린 item 예약
    public void createPersonReservation(ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Item item = itemRepository.findById(reservationDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        long minutesBetween = ChronoUnit.MINUTES.between(reservationDTO.getBorrowTime(), reservationDTO.getReturnTime());
        double hoursBetween = minutesBetween / 60.0; // 분을 시간으로 변환
        int totalPrice = (int) (hoursBetween * item.getPricePerHour());

        String itemRegisterDefaultProfile = item.getUser().getDefaultProfile(); //아이템 등록한사람의 프로필
        String imageUrl = item.getImageUrls().get(0); // 첫번쨰 이미지 추출

        Reservations reservation = Reservations.builder()
                .title(item.getTitle())
                .rentingStatus(item.getRentingStatus())
                .rentingStatus(RentingStatus.RESERVED)
                .borrowTime(reservationDTO.getBorrowTime())
                .returnTime(reservationDTO.getReturnTime())
                .totalPrice(totalPrice)
                .item(item)
                .itemUserNickName(item.getUser().getNickname())
                .itemBorrowPlace(item.getBorrowPlace())
                .itemReturnPlace(item.getReturnPlace())
                .userNickname(user.getNickname())
                .userPhoneNumber(user.getPhoneNumber())
                .itemRegisterDefaultProfile(itemRegisterDefaultProfile)
                .userDefaultProfile(user.getDefaultProfile()) // 예약하는 사람의 프로필
                .imageUrls(Collections.singletonList(imageUrl))
                .user(user) //대여하는 사람의 학번
                .itemUserId(item.getUser().getId()) //아이템 주인의 학번(id)
                .build();

        item.setReservation(Reservation.NO); // 예약 가능 상태를 NO로 설정 = 예약불가
        item.setRentingStatus(RentingStatus.RESERVED); // 예약완료
        reservationRepository.save(reservation);
    }

    //official이 올린 item 예약
    public void createOfficialReservation(ReservationDto.ReservationRequestDto reservationDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Item item = itemRepository.findById(reservationDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        String itemRegisterDefaultProfile = item.getUser().getDefaultProfile(); //아이템 등록한사람의 프로필
        String imageUrl = item.getImageUrls().get(0); // 첫번쨰 이미지 추출

        Reservations reservation = Reservations.builder()
                .title(item.getTitle())
                .rentingStatus(item.getRentingStatus())
                .rentingStatus(RentingStatus.RESERVED)
                .borrowTime(reservationDTO.getBorrowTime())
                .returnTime(reservationDTO.getReturnTime())
                .item(item)
                .itemUserNickName(item.getUser().getNickname())
                .itemBorrowPlace(item.getBorrowPlace())
                .itemReturnPlace(item.getReturnPlace())
                .user(user) //학번
                .userName(user.getName()) //이름
                .userPhoneNumber(user.getPhoneNumber())
                .userTrack1(user.getTrack1()) //트랙
                .userGrade(user.getGrade()) //4학년
                .userStatus(user.getStatus()) //복학
                .itemRegisterDefaultProfile(itemRegisterDefaultProfile)
                .userDefaultProfile(user.getDefaultProfile()) // 예약하는 사람의 프로필
                .imageUrls(Collections.singletonList(imageUrl))
                .build();

        item.setReservation(Reservation.NO); // 예약 가능 상태를 NO로 설정
        item.setRentingStatus(RentingStatus.RESERVED); // 예약완료
        reservationRepository.save(reservation);
    }
}
