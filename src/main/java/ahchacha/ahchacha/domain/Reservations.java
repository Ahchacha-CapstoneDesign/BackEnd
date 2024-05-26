package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import ahchacha.ahchacha.domain.common.enums.ToOwnerWrittenStatus;
import ahchacha.ahchacha.domain.common.enums.RentingStatus;
import ahchacha.ahchacha.domain.common.enums.ToRenterWrittenStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservations extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean notificationSentHour;
    private boolean notificationSentDay;

    private String title;
//    private String itemUserNickName; //아이템 등록한사람 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentingStatus rentingStatus;

    private LocalDateTime borrowTime; // 대여 시간과 반납 시간
    private LocalDateTime returnTime;
    private int totalPrice; // 총금액

//    private String userNickname;
    private String userPhoneNumber;

    private String itemBorrowPlace;
    private String itemReturnPlace;

    private String userName; //예약할사람의 이름
    private String userTrack1;
    private String userGrade;
    private String userStatus;

    private Long itemUserId; //아이템 등록한사람 id
//    private String itemRegisterDefaultProfile; //아이템 등록한사람 프사
//    private String userDefaultProfile; //아이템 예약하는사람 프사

    private ToRenterWrittenStatus toRenterWrittenStatus; //리뷰 쓴지(YES) 안쓴지(NO)
    private ToOwnerWrittenStatus toOwnerWrittenStatus;

    private boolean cancelStatus; // true 취소 됨 , false 취소 안됨

    @ElementCollection
    @CollectionTable(name = "reservations_images", joinColumns = @JoinColumn(name = "reservations_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "reservations", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "reservations", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Notification> notifications = new ArrayList<>();
}
