package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCommunityId(Long communityId);

    int countByParentId_Id(Long id);
}