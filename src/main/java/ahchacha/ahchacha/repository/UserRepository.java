package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User>findById(long id);

    Optional<User>findByNickname(String nickname);
}
