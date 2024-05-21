package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.Reservations;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.dto.ReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservations, Long> {
    Page<Reservations> findByUser(User user, Pageable pageable);

    Page<Reservations> findByUserAndRentingStatus(User user, RentingStatus rentingStatus, Pageable pageable);

    Page<Reservations> findByItemUserId(Long itemUserId, Pageable pageable);

    Page<Reservations> findByItemUserIdAndRentingStatus(Long itemUserId, RentingStatus rentingStatus, Pageable pageable);

    List<Reservations> findByUserAndReturnTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT r.item.category AS category, COUNT(r) AS reservationCount " +
            "FROM Reservations r " +
            "GROUP BY r.item.category " +
            "ORDER BY reservationCount DESC")
    List<Object[]> findTopCategoriesByReservationCount();
}
