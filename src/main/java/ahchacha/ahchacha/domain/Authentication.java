package ahchacha.ahchacha.domain;


import ahchacha.ahchacha.domain.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Authentication extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "authentication_images", joinColumns = @JoinColumn(name = "authentication_id"))
    @Column(name = "authentication_image_url")
    private List<String> authenticationImageUrls; //아이템 이미지

    private String name;
    private String track1;
    private String track2;
    private String phoneNumber;
    private String grade;
    private String status;

    private Boolean isCheck = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
