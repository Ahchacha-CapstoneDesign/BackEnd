package ahchacha.ahchacha.repository;

import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.Category;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Page<Item> findByUserAndReservation(User user, Reservation reservation, Pageable pageable);

    Page<Item> findByUserAndRentingStatus(User user, RentingStatus rentingStatus, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.title LIKE %:keyword% OR i.category = :category")
    Page<Item> findByTitleContainingOrCategory(@Param("keyword") String keyword, @Param("category") Category category, Pageable pageable);
    @Query("SELECT i FROM Item i WHERE i.category IN :categories ORDER BY i.viewCount DESC")
    List<Item> findTopItemsByCategories(List<Category> categories);

    @Query("SELECT i FROM Item i WHERE i.category = :category ORDER BY i.viewCount DESC")
    List<Item> findMyTopItemsByCategory(@Param("category") String category, Pageable pageable);
}