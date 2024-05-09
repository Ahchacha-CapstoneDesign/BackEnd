package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Builder
public class ReservationDto {
    @Getter
    @Setter
    @Builder
    public static class ReservationRequestDto { //person, official에 중복되는 값들
        private Long itemId;
        private LocalDateTime borrowTime;
        private LocalDateTime returnTime;
    }

    @Getter
    @Setter
    @Builder
    public static class ReservationResponseDto {
        private Long userId;
        private Long id;

        private List<String> imageUrls; //아이템사진

        private String title;

        private Long itemUserId;
        private String itemRegisterDefaultProfile; //아이템 등록한사람 프사
        private String itemUserNickName;

        private String itemBorrowPlace;
        private String itemReturnPlace;
        private LocalDateTime borrowTime; // 대여 시간과 반납 시간
        private LocalDateTime returnTime;

        private int totalPrice; // 총금액
        private RentingStatus rentingStatus;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReservationDto.ReservationResponseDto toDto(Reservations reservations) {
            return ReservationDto.ReservationResponseDto.builder()
                    .userId(reservations.getUser().getId())
                    .id(reservations.getId())

                    .imageUrls(reservations.getImageUrls())

                    .title(reservations.getTitle())

                    .itemUserId(reservations.getItemUserId())
                    .itemRegisterDefaultProfile(reservations.getItemRegisterDefaultProfile())
                    .itemUserNickName(reservations.getItemUserNickName())

                    .itemBorrowPlace(reservations.getItemBorrowPlace())
                    .itemReturnPlace(reservations.getItemReturnPlace())

                    .borrowTime(reservations.getBorrowTime())
                    .returnTime(reservations.getReturnTime())

                    .totalPrice(reservations.getTotalPrice())
                    .rentingStatus(reservations.getRentingStatus())

                    .createdAt(reservations.getCreatedAt())
                    .updatedAt(reservations.getUpdatedAt())

                    .build();
        }
    }

    public static Page<ReservationDto.ReservationResponseDto> toDtoPage(Page<Reservations> reservationsPage) {
        return reservationsPage.map(ReservationDto.ReservationResponseDto::toDto);
    }

}

