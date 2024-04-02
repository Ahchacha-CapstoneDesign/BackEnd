package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

    @Enumerated(EnumType.STRING)
    private PersonOrOfficial personOrOfficial; //공공 로그인 or 개인 로그인

    @Column(name = "default_profile")
    private String defaultProfile; //프로필사진

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();
}
