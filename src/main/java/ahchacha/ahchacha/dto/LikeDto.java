package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Community;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class LikeDto {

//    @Builder
//    @Getter
//    public static class LikeResponseDto {
//        private Long id;
//        private String nickname;
//        private Long targetId;
//        private int likeCount;
//
//        // community like
//        public static LikeResponseDto toDtoFromCommunity(Community community) {
//            return LikeResponseDto.builder()
//                    .id(community.getId())
//                    .nickname(community.getUser().getNickname())
//                    .targetId(community.getId())
//                    .postType(likeScrap.getPostType())
//                    .likeCount(likeScrap.getTalk().getLikeCount())
//                    .scrapCount(likeScrap.getTalk().getScrapCount())
//                    .build();
//        }
//    }
//
//    @Getter
//    @Builder
//    public static class CommentLikeScrapResponseDto {
//        private Long id;
//        private LikeScrapType likeScrapType;
//        private String nickname;
//        private Long targetId;  // Talk 또는 Post 또는 Job id
//        private PostType postType;
//        private int likeCount;
//
//        public static CommentLikeScrapResponseDto toDtoFromComment(LikeScrap likeScrap) {
//            return new CommentLikeScrapResponseDto(
//                    likeScrap.getId(),
//                    likeScrap.getLikeScrapType(),
//                    likeScrap.getUser().getNickname(),
//                    likeScrap.getComment().getId(),
//                    likeScrap.getPostType(),
//                    likeScrap.getComment().getLikeCount()
//            );
//        }
//    }
}
