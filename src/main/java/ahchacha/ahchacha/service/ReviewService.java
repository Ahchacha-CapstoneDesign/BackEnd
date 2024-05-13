package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.dto.ReviewDto;
import ahchacha.ahchacha.repository.ItemRepository;
import ahchacha.ahchacha.repository.ReservationRepository;
import ahchacha.ahchacha.repository.ReviewRepository;
import ahchacha.ahchacha.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Transactional //내가 등록한 물품을 예약했던 user에 대해 리뷰
    public ReviewDto.ReviewResponseDto createRenterReview(ReviewDto.ReviewRequestDto reviewDto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Reservations reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Review review = Review.builder()
                .reviewComment(reviewDto.getReviewComment())
                .reviewScore(reviewDto.getReviewScore())
                .personType(PersonType.TORENTER)

                .user(user)

                .itemOwnerId(reservation.getItemUserId())
                .ownerNickName(reservation.getItemUserNickName())
                .ownerProfile(reservation.getItemRegisterDefaultProfile())

                .renterUserId(reservation.getUser().getId())
                .renterNickName(reservation.getUserNickname())
                .renterProfile(reservation.getUserDefaultProfile())

                .reservations(reservation)
                .build();

        Review createdReview = reviewRepository.save(review);
        return ReviewDto.ReviewResponseDto.toDto(createdReview);
    }


    @Transactional //내가 반납완료한 아이템의 주인에게 리뷰
    public ReviewDto.ReviewResponseDto createOwnerReview(ReviewDto.ReviewRequestDto reviewDto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Reservations reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Review review = Review.builder()
                .reviewComment(reviewDto.getReviewComment())
                .reviewScore(reviewDto.getReviewScore())
                .personType(PersonType.TOOWNER)

                .user(user)

                .itemOwnerId(reservation.getItemUserId())
                .ownerNickName(reservation.getItemUserNickName())
                .ownerProfile(reservation.getItemRegisterDefaultProfile())

                .renterUserId(reservation.getUser().getId())
                .renterNickName(reservation.getUserNickname())
                .renterProfile(reservation.getUserDefaultProfile())

                .reservations(reservation)
                .build();

        Review createdReview = reviewRepository.save(review);

        // 리뷰가 작성될 때마다 해당 사용자의 리뷰 점수 업데이트
        updateAverageReviewScore(reservation.getItemUserId());

        return ReviewDto.ReviewResponseDto.toDto(createdReview);
    }
//    나 로그인 :
//    남이 나에게 리뷰한 내역(내가 등록한 물품)
//    현재 세션 로그인 사용자 = item_owner_id가 같은 리뷰 페이징 / personType = TOOWNER
    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getOtherCreateReviewToMeInMyRegisterItem(HttpSession session, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Long currentUserId = ((User) session.getAttribute("user")).getId();
        Page<Review> reviews = reviewRepository.findAllByItemOwnerIdAndPersonType(currentUserId, PersonType.TOOWNER, pageable);
        return ReviewDto.toDtoPage(reviews);
    }

//    나 로그인 :
//    남이 나에게 리뷰한 내역(내가 빌린 물품)
//    현재 세션 로그인 사용자 = renter_user_id / personType = TORENTER
    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getOtherCreateReviewToMeInMyRentedItem(HttpSession session, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Long currentUserId = ((User) session.getAttribute("user")).getId();
        Page<Review> reviews = reviewRepository.findAllByRenterUserIdAndPersonType(currentUserId, PersonType.TORENTER, pageable);
        return ReviewDto.toDtoPage(reviews);
    }

//    나 로그인 :
//    내가 남에게 리뷰한 내역(내가 빌린 물품의 주인에게)
//    현재 세션 로그인 사용자 = user_id  / personType = TOOWNER
    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getCreatedReviewToOwnerByMe(HttpSession session, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Long currentUserId = ((User) session.getAttribute("user")).getId();
        Page<Review> reviews = reviewRepository.findAllByUserIdAndPersonType(currentUserId, PersonType.TOOWNER, pageable);
        return ReviewDto.toDtoPage(reviews);
    }

//    나 로그인 :
//    내가 남에게 리뷰한 내역(내가 등록한 물품의 대여자에게)
//    현재 세션 로그인 사용자 = user_id  / personType = TORENTER
    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getCreatedReviewToRenterByMe(HttpSession session, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Long currentUserId = ((User) session.getAttribute("user")).getId();
        Page<Review> reviews = reviewRepository.findAllByUserIdAndPersonType(currentUserId, PersonType.TORENTER, pageable);
        return ReviewDto.toDtoPage(reviews);
    }

    private void updateAverageReviewScore(Long itemOwnerId) {
        // 아이템 주인의 모든 리뷰 점수를 가져와서 평균 계산
        List<Review> itemOwnerReviews = reviewRepository.findByItemOwnerId(itemOwnerId);
        double totalScore = 0;
        for (Review review : itemOwnerReviews) {
            totalScore += review.getReviewScore().doubleValue();
        }
        double averageScore = totalScore / itemOwnerReviews.size();

        // 계산된 평균 리뷰 점수를 아이템 주인 엔티티에 설정
        User itemOwner = userRepository.findById(itemOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Item owner not found"));
        itemOwner.setAverageReviewScore(BigDecimal.valueOf(averageScore));

        // 엔티티 업데이트
        userRepository.save(itemOwner);
    }
}
