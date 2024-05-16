package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.common.enums.*;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class ItemDto {
    @Getter
    @Setter
    @Builder
    public static class ItemRequestDto {
        private String title;
        private int pricePerHour;
        private LocalDateTime canBorrowDateTime;
        private LocalDateTime returnDateTime;
        private String borrowPlace;
        private String returnPlace;
        private String introduction;
        private ItemStatus itemStatus;
        private Category category;
    }

    @Getter
    @Setter
    @Builder
    public static class ItemResponseDto {
        private Long userId;
        private Long id;
        private String title;
        private int pricePerHour;
        private LocalDateTime canBorrowDateTime;
        private LocalDateTime returnDateTime;
        private String borrowPlace;
        private String returnPlace;
        private String introduction;
        private Reservation reservation;
        private RentingStatus rentingStatus;
        private ItemStatus itemStatus;
        private List<String> imageUrls;
        private Category category;
        private int viewCount;
        private PersonOrOfficial personOrOfficial;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String userProfile; //아이템 등록한사람 프로필
        private String userNickName; //아이템 등록한사람 닉네임

        private BigDecimal ownerReviewScore;
        private BigDecimal renterReviewScore;


        public static ItemResponseDto toDto(Item item) {
            return ItemResponseDto.builder()
                    .userId(item.getUser().getId())
                    .id(item.getId())
                    .title(item.getTitle())
                    .pricePerHour(item.getPricePerHour())
                    .canBorrowDateTime(item.getCanBorrowDateTime())
                    .returnDateTime(item.getReturnDateTime())
                    .borrowPlace(item.getBorrowPlace())
                    .returnPlace(item.getReturnPlace())
                    .introduction(item.getIntroduction())
                    .reservation(item.getReservation())
                    .rentingStatus(item.getRentingStatus())
                    .itemStatus(item.getItemStatus())
                    .imageUrls(item.getImageUrls())
                    .category(item.getCategory())
                    .viewCount(item.getViewCount())
                    .personOrOfficial(item.getPersonOrOfficial())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .userProfile(item.getUser().getDefaultProfile())
                    .userNickName(item.getUser().getNickname())

                    .ownerReviewScore(item.getUser().getOwnerReviewScore())
                    .renterReviewScore(item.getUser().getRenterReviewScore())

                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCountDto {
        private Category category;
        private int count;
    }

    public static Page<ItemResponseDto> toDtoPage(Page<Item> itemPage) {
        return itemPage.map(ItemResponseDto::toDto);
    }
}

