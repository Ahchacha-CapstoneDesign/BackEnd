package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.enums.AuthenticationValue;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    @Getter
    @Column(nullable = true, length = 15)
    private String nickname;

    private String name;

    private String track1;

    private String track2;

    private String phoneNumber;

    private String grade;

    private String status;

    private BigDecimal ownerReviewScore;

    private BigDecimal renterReviewScore;

    @Enumerated(EnumType.STRING)
    private PersonOrOfficial personOrOfficial; //공공 로그인 or 개인 로그인

    @Enumerated(EnumType.STRING)
    private AuthenticationValue authenticationValue = AuthenticationValue.NONE; //개인, 오피셜까지 가능, 관리자까지 가능

    @Column(name = "default_profile")
    private String defaultProfile; //프로필사진

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Reservations> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Community> communities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Authentication> authentication = new ArrayList<>();
}
