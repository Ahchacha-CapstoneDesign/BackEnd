package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservations extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String itemUserNickName; //아이템 등록한사람 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentingStatus rentingStatus;

    private LocalDateTime borrowTime; // 대여 시간과 반납 시간
    private LocalDateTime returnTime;
    private int totalPrice; // 총금액

    private String userNickname;
    private String userPhoneNumber;

    private String itemBorrowPlace;
    private String itemReturnPlace;

    private String userName; //예약할사람의 이름
    private String userTrack1;
    private String userGrade;
    private String userStatus;

    private String itemRegisterDefaultProfile; //아이템 등록한사람 프사
    private String userDefaultProfile; //아이템 예약하는사람 프사

    @ElementCollection
    @CollectionTable(name = "reservations_images", joinColumns = @JoinColumn(name = "reservations_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
