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
                    .reviewId(review.getId())
                    .reviewComment(review.getReviewComment())
                    .reviewScore(review.getReviewScore())

                    .itemOwnerId(review.getItemOwnerId())
                    .ownerNickName(review.getOwnerNickName())
                    .ownerProfile(review.getOwnerProfile())

                    .renterUserId(review.getRenterUserId())
                    .renterNickName(review.getRenterNickName())
                    .renterProfile(review.getRenterProfile())

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
