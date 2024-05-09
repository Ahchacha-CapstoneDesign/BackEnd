package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Comment;
import ahchacha.ahchacha.domain.Community;
import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.repository.CommentRepository;
import ahchacha.ahchacha.repository.CommunityRepository;
import ahchacha.ahchacha.repository.NotificationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private CommunityRepository communityRepository;
    private NotificationRepository notificationRepository;

    @Transactional
    public CommentDto.CommentResponseDto  createComment(CommentDto.CommentRequestDto commentDto, HttpSession session) {
        Optional<Community> community = communityRepository.findById(commentDto.getCommunityId());
        User user = (User) session.getAttribute("user");
        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .community(community.orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다.")))
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 댓글 수 업데이트
        community.get().setCommentCount(community.get().getCommentCount() + 1);
        communityRepository.save(community.get());

//        sendNotification(savedComment.getCommunity().getUser(), savedComment);

        return CommentDto.CommentResponseDto.toDto(savedComment);
    }

    @Transactional
    public CommentDto.CommentResponseDto updateComment(Long id, CommentDto.CommentRequestDto commentDto, User currentUser) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to update this comment.");
        }

        comment.updateContent(commentDto.getContent());
        return CommentDto.CommentResponseDto.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this comment.");
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.CommentResponseDto> getComments(Long communityId) {
        List<Comment> comments = commentRepository.findAllByCommunityId(communityId);

        return comments.stream().map(comment -> {
            int replyCount = commentRepository.countByParentId_Id(comment.getId()); // commentId와 일치하는 parentId 개수 반환
            // 좋아요 여부 확인
            //boolean likedByCurrentUser = likeScrapRepository.existsByUserAndCommentAndPostType(user, comment, PostType.COMMENT);

            return CommentDto.CommentResponseDto.toDto(comment, replyCount /*likedByCurrentUser*/);
        }).collect(Collectors.toList());
    }

    @Transactional
    public CommentDto.CommentResponseDto createReplyComment(CommentDto.ReplyRequestDto commentDto, HttpSession session) {
        Optional<Community> community = communityRepository.findById(commentDto.getCommunityId());
        User user = (User) session.getAttribute("user");
        Optional<Comment> parentId = commentRepository.findById(commentDto.getParentId());
        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .community(community.orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다.")))
                .user(user)
                .parentId(parentId.orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다.")))
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 댓글 수 업데이트
        community.get().setReplyCount(community.get().getReplyCount() + 1);
        communityRepository.save(community.get());
//
//        sendNotification(savedComment.getCommunity().getUser(), savedComment);

        return CommentDto.CommentResponseDto.toDto(savedComment);
    }

    private void sendNotification(User user, Comment comment) {
        Notification notification = Notification.builder()
                .user(user)
                .comment(comment)
                .isRead(false)  // 초기에 알림은 읽지 않음
                .build();

        notificationRepository.save(notification);
    }
}
