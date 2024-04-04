package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.dto.CommentDto;
import ahchacha.ahchacha.dto.ReviewDto;
import ahchacha.ahchacha.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping@Operation(summary = "댓글 작성")
    public CommentDto.CommentResponseDto createComment(@RequestBody CommentDto.CommentRequestDto commentRequestDto, HttpSession session) {

        return commentService.createComment(commentRequestDto, session);
    }

    @Operation(summary = "댓글 수정", description = "{id} 자리에 수정할 댓글 id를 전달해주세요.")
    @PatchMapping("/{id}")
    public CommentDto.CommentResponseDto  updateComment(@PathVariable Long id, @RequestBody CommentDto.CommentRequestDto commentRequestDto) {
        return commentService.updateComment(id, commentRequestDto);
    }
    @Operation(summary = "댓글 삭제", description = "{id} 자리에 삭제할 댓글 id를 전달해주세요.")
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @Operation(summary = "댓글 조회")
    @GetMapping("/community/{communityId}")
    public List<CommentDto.CommentResponseDto> getComments(@PathVariable Long communityId, HttpSession session) {
        return commentService.getComments(communityId, session);
    }

    @Operation(summary = "답글 작성")
    @PostMapping("/reply")
    public CommentDto.CommentResponseDto createReplyComment(@RequestBody CommentDto.ReplyRequestDto replyRequestDto, HttpSession session) {

        return commentService.createReplyComment(replyRequestDto, session);
    }
}
