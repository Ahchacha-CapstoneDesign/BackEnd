package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Page<Community> findByTitleContaining(String title, Pageable pageable);

    Page<Community> findByContentContaining(String content, Pageable pageable);

}
