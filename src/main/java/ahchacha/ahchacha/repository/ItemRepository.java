package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndBorrowPlaceAndReturnPlace(Long id, String borrowPlace, String returnPlace);

    Page<Item> findByUser(User user, Pageable pageable);

    Page<Item> findAll(Pageable pageable);

    Page<Item> findByTitleContaining(String title, Pageable pageable);

    Page<Item> findByCategory(Category category, Pageable pageable);

    @Query("SELECT i.category, SUM(i.viewCount) FROM Item i GROUP BY i.category ORDER BY SUM(i.viewCount) DESC")
    List<Object[]> findTopCategoriesByViewCount(Pageable pageable);

//    Page<Item> findByTitleContainingOrCategory(String title, Category category, Pageable pageable);
}