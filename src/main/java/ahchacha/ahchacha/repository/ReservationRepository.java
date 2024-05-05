package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservations, Long> {

}
