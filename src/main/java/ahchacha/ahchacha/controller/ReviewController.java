package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.dto.ReviewDto;
import ahchacha.ahchacha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "내가 등록한 물품을 예약했던 user에 대해 리뷰")
    @PostMapping("/toRenter")
    public ResponseEntity<ReviewDto.ReviewResponseDto> createRenterReview(@RequestBody ReviewDto.ReviewRequestDto reviewRequestDto, HttpSession session) {

        ReviewDto.ReviewResponseDto reviewResponseDto = reviewService.createRenterReview(reviewRequestDto, session);
        return new ResponseEntity<>(reviewResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "내가 반납완료한 아이템의 주인에게 리뷰")
    @PostMapping("/toOwner")
    public ResponseEntity<ReviewDto.ReviewResponseDto> createOwnerReview(@RequestBody ReviewDto.ReviewRequestDto reviewRequestDto, HttpSession session) {

        ReviewDto.ReviewResponseDto reviewRentedResponseDto = reviewService.createOwnerReview(reviewRequestDto, session);
        return new ResponseEntity<>(reviewRentedResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "나 로그인 : 남이 나에게 리뷰한 내역(내가 등록한 물품)",
            description = "나 로그인 : \n\n" + "남이 나에게 리뷰한 내역(내가 등록한 물품)\n\n"
                    + "현재 세션 로그인 사용자 = item_owner_id / personType = TOOWNER")
    @GetMapping("/getOtherCreateReviewToMeInMyRegisterItem")
    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getOtherCreateReviewToMeInMyRegisterItem(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getOtherCreateReviewToMeInMyRegisterItem(session, page);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "나 로그인 : 남이 나에게 리뷰한 내역(내가 빌린 물품)",
            description = "나 로그인 : \n\n" + "남이 나에게 리뷰한 내역(내가 빌린 물품)\n\n"
                    + "현재 세션 로그인 사용자 = renter_user_id / personType = TORENTER")
    @GetMapping("/getOtherCreateReviewToMeInMyRentedItem")
    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getOtherCreateReviewToMeInMyRentedItem(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getOtherCreateReviewToMeInMyRentedItem(session, page);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "나 로그인 : 내가 남에게 리뷰한 내역(내가 빌린 물품의 주인에게)",
            description = "나 로그인 : \n\n" +"내가 남에게 리뷰한 내역(내가 빌린 물품의 주인에게)\n\n"
                    + "현재 세션 로그인 사용자 = user_id  / personType = TOOWNER")
    @GetMapping("/getCreatedReviewToOwnerByMe")
    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getCreatedReviewToOwnerByMe(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getCreatedReviewToOwnerByMe(session, page);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "나 로그인 : 내가 남에게 리뷰한 내역(내가 등록한 물품의 대여자에게)",
            description = "나 로그인 : \n\n" + "내가 남에게 리뷰한 내역(내가 등록한 물품의 대여자에게)\n\n"
                    + "현재 세션 로그인 사용자 = user_id  / personType = TORENTER")
    @GetMapping("/getCreatedReviewToRenterByMe")
    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getCreatedReviewToRenterByMe(@RequestParam(value = "page", defaultValue = "1") int page, HttpSession session) {
        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getCreatedReviewToRenterByMe(session, page);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "아이템 상세의 등록자로서 받은 리뷰(대여자 -> 등록자(아이템등록자)")
    @GetMapping("/reviewDetails/{itemOwnerId}")
    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getReviewsByItemOwnerId(@PathVariable Long itemOwnerId,
                                                                                     @RequestParam(value = "page", defaultValue = "1") int page) {
        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getReviewsByItemOwnerId(itemOwnerId, page);
        return ResponseEntity.ok(reviews);
    }


    @Operation(summary = "리뷰 상세 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto.ReviewResponseDto> getReviewById(@PathVariable Long reviewId) {
        Optional<ReviewDto.ReviewResponseDto> optionalReviewResponseDto = reviewService.getReviewById(reviewId);

        return optionalReviewResponseDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getLatestAndHighestScoreReviews")
    public ResponseEntity<List<ReviewDto.ReviewResponseDto>> getLatestAndHighestScoreReviews(HttpSession session) {
        List<ReviewDto.ReviewResponseDto> reviews = reviewService.getLatestAndHighestScoreReviews(session);
        return ResponseEntity.ok(reviews);
    }
}

