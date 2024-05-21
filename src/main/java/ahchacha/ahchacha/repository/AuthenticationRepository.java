package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    List<Authentication> findByUserId(Long userId);
}
