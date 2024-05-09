package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.*;
import ahchacha.ahchacha.dto.HeartDto;
import ahchacha.ahchacha.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@AllArgsConstructor
@Service
public class HeartService {
    private final HeartRepository heartRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public Optional<Heart> checkIfUserLiked(User user, Community community){
        return heartRepository.findByUserAndCommunity(user, community);
    }

    @Transactional
    public HeartDto.CommunityLikeResponseDto addCommunityLike(Long communityId, HttpSession session) throws Exception{

        User user = (User) session.getAttribute("user");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<Heart> likeOptional = checkIfUserLiked(user, community);
        if(likeOptional.isPresent()){
            throw new Exception("이미 좋아요한 게시글입니다.");
        }

        Heart heart = Heart.builder()
                .user(user)
                .community(community)
                .build();

        heartRepository.save(heart);

        community.setLikeCount(community.getLikeCount() + 1 );
        communityRepository.save(community);

//        sendNotification(heart.getCommunity().getUser(), heart);

        return HeartDto.CommunityLikeResponseDto.toDtoFromCommunity(heart);
    }

    @Transactional
    public void deleteCommunityLike(Long communityId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //좋아요 여부 확인
        Optional<Heart> likeOptional = checkIfUserLiked(user, community);
        if (likeOptional.isPresent()) {
            heartRepository.delete(likeOptional.get());

            // 좋아요 수 업데이트
            community.setLikeCount(community.getLikeCount() - 1);
            communityRepository.save(community);
        }
    }

    public Boolean validateIfUserLikedCommunity(Long communityId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("개시글을 찾을 수 없습니다."));

        Optional<Heart> likeOptional = checkIfUserLiked(user, community);
        return likeOptional.isPresent();
    }

    public Optional<Heart> checkIfUserLikedComment(User user, Comment comment) {
        return heartRepository.findByUserAndComment(user, comment);
    }

    public HeartDto.CommentLikeResponseDto addCommentLike(Long commentId, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 좋아요 여부 확인
        Optional<Heart> likeOptional = checkIfUserLikedComment(user, comment);
        if (likeOptional.isPresent()) {
            throw new Exception("이미 좋아요한 댓글 입니다.");
        }

        Heart heart = Heart.builder()
                .user(user)
                .comment(comment)
                .build();

        heartRepository.save(heart);

        // 좋아요 수 업데이트
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);


        return HeartDto.CommentLikeResponseDto.toDtoFromComment(heart);
    }

    @Transactional
    public void deleteCommentLike(Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        //좋아요 여부 확인
        Optional<Heart> likeOptional = checkIfUserLikedComment(user, comment);
        if (likeOptional.isPresent()) {
            heartRepository.delete(likeOptional.get());

            // 좋아요 수 업데이트
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
        }
    }

    private void sendNotification(User user, Heart heart) {
        Notification notification = Notification.builder()
                .user(user)
                .heart(heart)
                .isRead(false)  // 초기에 알림은 읽지 않음
                .build();

        notificationRepository.save(notification);
    }

    public Boolean validateIfUserLikedComment(Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        Optional<Heart> likeOptional = checkIfUserLikedComment(user, comment);
        return likeOptional.isPresent();
    }


}
