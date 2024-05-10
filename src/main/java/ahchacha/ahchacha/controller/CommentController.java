package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.dto.CommunityDto;
import ahchacha.ahchacha.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<CommentDto.CommentResponseDto> createComment(@RequestBody CommentDto.CommentRequestDto commentRequestDto, HttpSession session) {
        CommentDto.CommentResponseDto responseDto = commentService.createComment(commentRequestDto, session);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "답글 작성")
    @PostMapping("/reply")
    public ResponseEntity<CommentDto.CommentResponseDto> createReplyComment(@RequestBody CommentDto.ReplyRequestDto replyRequestDto, HttpSession session) {
        CommentDto.CommentResponseDto responseDto = commentService.createReplyComment(replyRequestDto, session);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 수정", description = "{id} 자리에 수정할 댓글 id를 전달해주세요.")
    @PatchMapping("/{id}")
    public CommentDto.CommentResponseDto  updateComment(@PathVariable Long id, @RequestBody CommentDto.CommentRequestDto commentRequestDto, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        return commentService.updateComment(id, commentRequestDto, user);
    }
    @Operation(summary = "댓글 삭제", description = "{id} 자리에 삭제할 댓글 id를 전달해주세요.")
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        commentService.deleteComment(id, user);
    }

    @Operation(summary = "댓글 조회")
    @GetMapping("/community/{communityId}")
    public List<CommentDto.CommentResponseDto> getComments(@PathVariable Long communityId) {
        return commentService.getComments(communityId);
    }
}
