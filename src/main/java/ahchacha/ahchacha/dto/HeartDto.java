package ahchacha.ahchacha.dto;


import ahchacha.ahchacha.domain.Heart;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class HeartDto {
    @Builder
    @Getter
    public static class CommunityLikeResponseDto{
        private Long id;
        private String nickname;
        private Long communityId;
        private int likeCount;

        public static CommunityLikeResponseDto toDtoFromCommunity(Heart heart){
            return CommunityLikeResponseDto.builder()
                    .id(heart.getId())
                    .nickname(heart.getUser().getNickname())
                    .communityId(heart.getCommunity().getId())
                    .likeCount(heart.getCommunity().getLikeCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentLikeResponseDto {
        private Long id;
        private String nickname;
        private Long commentId;
        private int likeCount;

        public static CommentLikeResponseDto toDtoFromComment(Heart heart){
            return new CommentLikeResponseDto(
                    heart.getId(),
                    heart.getUser().getNickname(),
                    heart.getComment().getId(),
                    heart.getComment().getLikeCount()
            );
        }
    }
}
