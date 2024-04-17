package ahchacha.ahchacha.controller;
import ahchacha.ahchacha.dto.HeartDto;
import ahchacha.ahchacha.service.HeartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/likes") // 기본 URL 경로 설정
public class HeartController {

    private final HeartService heartService;

    @Operation(summary = "게시글에 좋아요", description = "{communityId} 자리에 좋아요할 게시글 id를 전달해주세요.")
    @PostMapping("/likes/community/{communityId}")
    public ResponseEntity<HeartDto.CommunityLikeResponseDto> addLike(@PathVariable Long communityId, HttpSession session) throws Exception {
        HeartDto.CommunityLikeResponseDto likeDto = heartService.addCommunityLike(communityId, session);
        return ResponseEntity.ok(likeDto);
    }
    @Operation(summary = "게시글에 좋아요 삭제 ", description = "{communityId} 자리에 좋아요를 삭제할 게시글 id를 전달해주세요.")
    @DeleteMapping("/likes/community/{communityId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long communityId, HttpSession session) {
        heartService.deleteCommunityLike(communityId, session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글에 좋아요", description = "{commentId} 자리에 좋아요할 댓글 id를 전달해주세요.")
    @PostMapping("/likes/comment/{commentId}")
    public ResponseEntity<HeartDto.CommentLikeResponseDto> addLikeComment(@PathVariable Long commentId, HttpSession session) throws Exception {
        HeartDto.CommentLikeResponseDto likeDto = heartService.addCommentLike(commentId, session);
        return ResponseEntity.ok(likeDto);
    }
    @Operation(summary = "댓글에 좋아요 삭제 ", description = "{commentId} 자리에 좋아요를 삭제할 댓글 id를 전달해주세요.")
    @DeleteMapping("/likes/comment/{commentId}")
    public ResponseEntity<Void> deleteLikeComment(@PathVariable Long commentId, HttpSession session) {
        heartService.deleteCommentLike(commentId, session);
        return ResponseEntity.ok().build();
    }
}
