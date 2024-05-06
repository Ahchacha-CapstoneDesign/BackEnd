package ahchacha.ahchacha.dto;

import lombok.*;

import java.time.LocalDateTime;

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

//    @Getter
//    @Setter
//    @Builder
//    public static class ItemResponseDto {
//
//    }
}

