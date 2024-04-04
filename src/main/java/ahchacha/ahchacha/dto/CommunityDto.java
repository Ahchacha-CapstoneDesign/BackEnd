package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Community;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class CommunityDto {
    @Getter
    @Builder
    public static class CommunityRequestDto{
        private String title;
        private String content;
        private List<String> pictureUrls;
    }

    @Getter
    @Setter
    @Builder
    public static class CommunityResponseDto{
        private Long id;
        private String title;
        private String content;
        private List<String> imageUrls;
        private int viewCount;
        private int likeCount;
        private int commentCount;
        private int replyCount;
        private String nickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CommunityResponseDto toDto(Community community){
            return CommunityResponseDto.builder()
                    .id(community.getId())
                    .title(community.getTitle())
                    .content(community.getContent())
                    .imageUrls(community.getImageUrls())
                    .viewCount(community.getViewCount())
                    .likeCount(community.getLikeCount())
                    .commentCount((community.getCommentCount()))
                    .replyCount(community.getReplyCount())
                    .nickname(community.getUser().getNickname())
                    .createdAt(community.getCreatedAt())
                    .updatedAt(community.getUpdatedAt())
                    .build();
        }
    }

    public static Page<CommunityResponseDto> toDtoPage(Page<Community> communityPage){
        return communityPage.map(CommunityResponseDto::toDto);
    }
}