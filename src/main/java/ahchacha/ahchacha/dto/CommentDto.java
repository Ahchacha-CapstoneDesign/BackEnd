package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Comment;
import lombok.Data;
import lombok.*;


import java.time.LocalDateTime;

@Data
public class CommentDto {

    @Getter
    public static class CommentRequestDto {
        private Long communityId;
        private String content;
    }
    @Getter
    public static class ReplyRequestDto {
        private Long communityId;
        private Long parentId;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    public static class CommentResponseDto {
        private Long communityId;
        private Long id;
        private Long parentId;
        private int likeCount;
        private String content;
        private String nickname;
        private String profileUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int replyCount;

        public static CommentResponseDto toDto(Comment comment) {
            return CommentResponseDto.builder()
                    .communityId(comment.getCommunity().getId())
                    .id(comment.getId())
                    .parentId(comment.getParentId() != null ? comment.getParentId().getId() : null)
                    .likeCount(comment.getLikeCount())
                    .content(comment.getContent())
                    .nickname(comment.getUser().getNickname())
                    .profileUrl(comment.getUser().getDefaultProfile())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        }

        public static CommentResponseDto toDto(Comment comment, int replyCount) {
            return CommentResponseDto.builder()
                    .communityId(comment.getCommunity().getId())
                    .id(comment.getId())
                    .parentId(comment.getParentId() != null ? comment.getParentId().getId() : null)
                    .likeCount(comment.getLikeCount())
                    .content(comment.getContent())
                    .nickname(comment.getUser().getNickname())
                    .profileUrl(comment.getUser().getDefaultProfile())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .replyCount(replyCount)
                    .build();
        }
    }

}