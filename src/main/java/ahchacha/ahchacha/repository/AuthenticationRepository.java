package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {

}
