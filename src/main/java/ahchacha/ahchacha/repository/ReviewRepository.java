package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Page<Review> findAllByItemOwnerIdAndPersonType(Long itemOwnerId, PersonType personType, Pageable pageable);

    Page<Review> findAllByRenterUserIdAndPersonType(Long renterUserId, PersonType personType, Pageable pageable);

    Page<Review> findAllByUserIdAndPersonType(Long userId, PersonType personType, Pageable pageable);

    List<Review> findByItemOwnerId(Long itemOwnerId);

    List<Review> findByRenterUserId(Long renterUserId);

    Page<Review> findAllByItemOwnerIdAndPersonTypeOrderByCreatedAtDesc(Long itemOwnerId, PersonType personType, Pageable pageable);

}
