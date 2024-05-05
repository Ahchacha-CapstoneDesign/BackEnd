package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import lombok.*;
import org.springframework.data.domain.Page;
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
//        private Reservation reservation;
//        private RentingStatus rentingStatus;
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
        private List<String> imageUrls;
        private Category category;
        private int viewCount;
        private PersonOrOfficial personOrOfficial;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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
                    .imageUrls(item.getImageUrls())
                    .category(item.getCategory())
                    .viewCount(item.getViewCount())
                    .personOrOfficial(item.getPersonOrOfficial())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
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

