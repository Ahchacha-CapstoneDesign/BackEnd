package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
