package ahchacha.ahchacha.service;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import ahchacha.ahchacha.domain.common.enums.ToOwnerWrittenStatus;
import ahchacha.ahchacha.domain.common.enums.ToRenterWrittenStatus;
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

        reservation.setToRenterWrittenStatus(ToRenterWrittenStatus.YES);

        updateAverageReviewScoreForRenter(reservation.getUser().getId());


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

        reservation.setToOwnerWrittenStatus(ToOwnerWrittenStatus.YES);
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

    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getReviewsByItemOwnerId(Long itemOwnerId, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));
        // itemOwnerId에 대해 TOOWNER인 리뷰를 가져옵니다.
        Page<Review> reviews = reviewRepository.findAllByItemOwnerIdAndPersonType(itemOwnerId, PersonType.TOOWNER, pageable);
        // ReviewResponseDto로 변환하여 반환합니다.
        return ReviewDto.toDtoPage(reviews);
    }

    @Transactional
    public Page<ReviewDto.ReviewResponseDto> getReviewsByRenterUserId(Long renterUserId, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 1000, Sort.by(sorts));

        Page<Review> reviews = reviewRepository.findAllByRenterUserIdAndPersonType(renterUserId, PersonType.TORENTER, pageable);

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
        itemOwner.setOwnerReviewScore(BigDecimal.valueOf(averageScore));

        // 엔티티 업데이트
        userRepository.save(itemOwner);
    }

    private void updateAverageReviewScoreForRenter(Long renterUserId) {
        // 물품을 예약한 사용자의 모든 리뷰 점수를 가져와서 평균 계산
        List<Review> renterReviews = reviewRepository.findByRenterUserId(renterUserId);
        double totalScore = 0;
        for (Review review : renterReviews) {
            totalScore += review.getReviewScore().doubleValue();
        }
        double averageScore = totalScore / renterReviews.size();

        // 계산된 평균 리뷰 점수를 사용자 엔티티에 설정
        User renter = userRepository.findById(renterUserId)
                .orElseThrow(() -> new IllegalArgumentException("Renter not found"));
        renter.setRenterReviewScore(BigDecimal.valueOf(averageScore));

        // 엔티티 업데이트
        userRepository.save(renter);
    }

    public Optional<ReviewDto.ReviewResponseDto> getReviewById(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        return optionalReview.map(ReviewDto.ReviewResponseDto::toDto);
    }

    public List<ReviewDto.ReviewResponseDto> getLatestAndHighestScoreReviews(HttpSession session) {
        List<ReviewDto.ReviewResponseDto> reviews = new ArrayList<>();

        Long currentUserId = ((User) session.getAttribute("user")).getId();

        // 내가 등록한 물품의 리뷰 중 가장 최신 및 reviewScore가 가장 높은 리뷰 가져오기
        Pageable pageable1 = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("reviewScore")));
        Page<Review> reviews1 = reviewRepository.findAllByItemOwnerIdAndPersonType(currentUserId, PersonType.TOOWNER, pageable1);
        reviews1.getContent().stream()
                .findFirst()
                .map(ReviewDto.ReviewResponseDto::toDto)
                .ifPresent(reviews::add);

        // 내가 빌린 물품의 리뷰 중 가장 최신 및 reviewScore가 가장 높은 리뷰 가져오기
        Pageable pageable2 = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("reviewScore")));
        Page<Review> reviews2 = reviewRepository.findAllByRenterUserIdAndPersonType(currentUserId, PersonType.TORENTER, pageable2);
        reviews2.getContent().stream()
                .findFirst()
                .map(ReviewDto.ReviewResponseDto::toDto)
                .ifPresent(reviews::add);

        return reviews;
    }
}
