package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Builder
public class ReviewDto {
    @Getter
    @Setter
    @Builder
    public static class ReviewRequestDto {
        private Long reservationId;
        private String reviewComment;
        private BigDecimal reviewScore;
    }

    @Getter
    @Setter
    @Builder
    public static class ReviewResponseDto {
        private Long itemId;
        private String itemTitle;
        private Long reservationId;

        private Long reviewId;
        private String reviewComment;
        private BigDecimal reviewScore;

        private Long itemOwnerId;
        private String ownerNickName;
        private String ownerProfile;

        private Long renterUserId;
        private String renterNickName;
        private String renterProfile;

        private PersonType personType;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReviewResponseDto toDto(Review review) {
            return ReviewResponseDto.builder()
                    .itemId(review.getReservations().getItem().getId())
                    .itemTitle(review.getReservations().getItem().getTitle())
                    .reservationId(review.getReservations().getId())

                    .reviewId(review.getId())
                    .reviewComment(review.getReviewComment())
                    .reviewScore(review.getReviewScore())

                    .itemOwnerId(review.getItemOwnerId())
                    .ownerNickName(review.getReservations().getItem().getUser().getNickname())
                    .ownerProfile(review.getReservations().getItem().getUser().getDefaultProfile())

                    .renterUserId(review.getRenterUserId())
                    .renterNickName(review.getUser().getNickname())
                    .renterProfile(review.getUser().getDefaultProfile())

                    .personType(review.getPersonType())

                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }
    }

    public static Page<ReviewResponseDto> toDtoPage(Page<Review> itemReviewPage) {
        return itemReviewPage.map(ReviewResponseDto::toDto);
    }
}
