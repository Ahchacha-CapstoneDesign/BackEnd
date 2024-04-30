package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Review;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Page<Review> findByPersonType(PersonType personType, Pageable pageable);

    Optional<Review> findByItemId(Long itemId);

    Page<Review> findByUserIdAndPersonType(Long userId, PersonType personType, Pageable pageable);

    @Query("SELECT AVG(r.reviewScore) FROM Review r WHERE r.user.id = :userId AND r.personType = :personType")
    BigDecimal findAverageScoreByUserIdAndPersonType(@Param("userId") Long userId, @Param("personType") PersonType personType);

    @Query("SELECT AVG(r.reviewScore) FROM Review r WHERE r.user.id = :userId")
    BigDecimal findAverageScoreByUserId(@Param("userId") Long userId);
}
