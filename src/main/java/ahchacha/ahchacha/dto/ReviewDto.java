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
    public static class ReviewResponseDto { //내가 등록한 물품을 빌린 사람에 대해
        private Long reviewId;
        private String reviewComment;
        private BigDecimal reviewScore;

        private PersonType personType;
        private String renterNickName;
        private String renterProfile;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReviewResponseDto toDto(Review review) {
            return ReviewResponseDto.builder()
                    .reviewId(review.getId())
                    .reviewComment(review.getReviewComment())
                    .reviewScore(review.getReviewScore())

                    .personType(PersonType.RENTER)
                    .renterNickName(review.getRenterNickName())
                    .renterProfile(review.getRenterProfile())

                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }
    }

    public static Page<ReviewResponseDto> toDtoPage(Page<Review> itemReviewPage) {
        return itemReviewPage.map(ReviewResponseDto::toDto);
    }
}
