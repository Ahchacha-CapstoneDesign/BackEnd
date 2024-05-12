package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import ahchacha.ahchacha.dto.ReviewDto;
import ahchacha.ahchacha.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "내가 등록한 물품을 예약했던 user에 대해 리뷰")
    @PostMapping("/toRenter")
    public ResponseEntity<ReviewDto.ReviewResponseDto> createRenterReview(@RequestBody ReviewDto.ReviewRequestDto reviewRequestDto, HttpSession session) {

        ReviewDto.ReviewResponseDto reviewResponseDto = reviewService.createMyItemReview(reviewRequestDto, session);
        return new ResponseEntity<>(reviewResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "내가 반납완료한 아이템의 주인에게 리뷰")
    @PostMapping("/toOwner")
    public ResponseEntity<ReviewDto.ReviewRentedResponseDto> createOwnerReview(@RequestBody ReviewDto.ReviewRequestDto reviewRequestDto, HttpSession session) {

        ReviewDto.ReviewRentedResponseDto reviewRentedResponseDto = reviewService.createUserReview(reviewRequestDto, session);
        return new ResponseEntity<>(reviewRentedResponseDto, HttpStatus.CREATED);
    }

//    @Operation(summary = "리뷰 최신 목록 조회 RENTER")
//    @GetMapping("/list-renter")
//    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getAllReviewsRENTER(@RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<ReviewDto.ReviewResponseDto> reviewsResponseDto = reviewService.getAllReviewsRENTER(page);
//        return new ResponseEntity<>(reviewsResponseDto, HttpStatus.OK);
//    }
//
//    @Operation(summary = "리뷰 최신 목록 조회 RECEIVER")
//    @GetMapping("/list-receiver")
//    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getAllReviewsRECEIVER(@RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<ReviewDto.ReviewResponseDto> reviewsResponseDto = reviewService.getAllReviewsRECEIVER(page);
//        return new ResponseEntity<>(reviewsResponseDto, HttpStatus.OK);
//    }
//
//    @Operation(summary = "아이템 아이디로 리뷰 조회", description = "{itemId} 자리에 상세 조회할 아이템 id를 전달해주세요.")
//    @GetMapping("/{itemId}")
//    public ResponseEntity<ReviewDto.ReviewResponseDto> getReviewByItemId(@PathVariable Long itemId) {
//        ReviewDto.ReviewResponseDto review = reviewService.getReviewByItemId(itemId);
//        return ResponseEntity.ok(review);
//    }
//
//    @Operation(summary = "유저 아이디로 별점 높은순 2개 조회", description = "{userId} 자리에 userId를 전달해주세요.")
//    @GetMapping("/{userId}/renter_shortView") //피그마 대여물건상세페이지 유저 밑에 2개 리뷰에 쓰기
//    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getReviewsByUserIdAndPersonTypeRENTERShortView(
//            @PathVariable Long userId,
//            @RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getReviewsByUserIdAndPersonTypeRENTERShortView(userId, page);
//        return new ResponseEntity<>(reviews, HttpStatus.OK);
//    }
//
//    @Operation(summary = "유저 아이디로 RENTER 별점 높은순 조회", description = "{userId} 자리에 userId를 전달해주세요.")
//    @GetMapping("/{userId}/renter_RENTER")
//    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getReviewsByUserIdAndPersonTypeRENTER(
//            @PathVariable Long userId,
//            @RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getReviewsByUserIdAndPersonTypeRENTER(userId, page);
//        return new ResponseEntity<>(reviews, HttpStatus.OK);
//    }
//
//    @Operation(summary = "유저 아이디로 RECEIVER 별점 높은순 조회", description = "{userId} 자리에 userId를 전달해주세요.")
//    @GetMapping("/{userId}/renter_RECEIVER")
//    public ResponseEntity<Page<ReviewDto.ReviewResponseDto>> getReviewsByUserIdAndPersonTypeRECEIVER(
//            @PathVariable Long userId,
//            @RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<ReviewDto.ReviewResponseDto> reviews = reviewService.getReviewsByUserIdAndPersonTypeRECEIVER(userId, page);
//        return new ResponseEntity<>(reviews, HttpStatus.OK);
//    }
//
//    @GetMapping("/{userId}/average/{personType}")
//    public ResponseEntity<BigDecimal> getAverageScoreByPersonType(
//            @PathVariable Long userId,
//            @PathVariable PersonType personType) {
//        BigDecimal averageScore = reviewService.getAverageScoreByUserIdAndPersonType(userId, personType);
//        return ResponseEntity.ok(averageScore);
//    }
//
//    @GetMapping("/{userId}/average")
//    public ResponseEntity<BigDecimal> getOverallAverageScore(
//            @PathVariable Long userId) {
//        BigDecimal overallAverageScore = reviewService.getOverallAverageScoreByUserId(userId);
//        return ResponseEntity.ok(overallAverageScore);
//    }
//
//    @Operation(summary = "리뷰 삭제", description = "review id를 입력하세요, 로그인 한 사용자의 리뷰가 아니면 삭제가 되지않습니다.")
//    @DeleteMapping("{reviewId}")
//    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute("user");
//        reviewService.deleteReview(reviewId, user);
//        return ResponseEntity.ok().build();
//    }
}

