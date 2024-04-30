package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Comment;
import ahchacha.ahchacha.domain.Community;
import ahchacha.ahchacha.domain.Heart;
import ahchacha.ahchacha.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByUserAndCommunity(User user, Community community);
    Optional<Heart> findByUserAndComment(User user, Comment comment);

}
