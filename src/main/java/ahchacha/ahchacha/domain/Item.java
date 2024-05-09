package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import ahchacha.ahchacha.domain.common.enums.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; //제목

    @Column(name = "pricePerHour")
    private int pricePerHour; //대여비

    @Column(name = "canBorrowDateTime")
    private LocalDateTime canBorrowDateTime; //대여가능 날짜,시간

    @Column(name = "returnDateTime")
    private LocalDateTime returnDateTime; // 반납 날짜, 시간

    @Column(nullable = false)
    private String borrowPlace; //대여 장소

    @Column(nullable = false)
    private String returnPlace; //반납 장소

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String introduction; //설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reservation reservation; // 예약 가능, 불가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentingStatus rentingStatus; // NONE, 예약완료, 대여중, 반납완료

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus; //새상품, 사용감 없음, 사용감적음, 사용감많음, 파손/고장상품

    @ElementCollection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url")
    private List<String> imageUrls; //아이템 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; //아이템 카테고리

    @Setter
    @ColumnDefault("0")
    @Column(name = "view_count")
    private int viewCount;

    @Enumerated(EnumType.STRING)
    private PersonOrOfficial personOrOfficial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservations> reservations = new ArrayList<>();

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private Review review;

}
