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
        private Long itemId;
        private String reviewComment;
        private BigDecimal reviewScore;
        private PersonType personType;
    }

    @Getter
    @Setter
    @Builder
    public static class ReviewResponseDto {
        private String nickname;
        private Long reviewId;
        private Long itemId;
        private String reviewComment;
        private BigDecimal reviewScore;
        private PersonType personType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReviewResponseDto toDto(Review review) {
            return ReviewResponseDto.builder()
                    .nickname(review.getUser().getNickname())
                    .reviewId(review.getId())
                    .itemId(review.getItem().getId())
                    .reviewComment(review.getReviewComment())
                    .reviewScore(review.getReviewScore())
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
