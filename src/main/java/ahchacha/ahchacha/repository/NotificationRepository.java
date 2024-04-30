package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Notification;
import ahchacha.ahchacha.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
