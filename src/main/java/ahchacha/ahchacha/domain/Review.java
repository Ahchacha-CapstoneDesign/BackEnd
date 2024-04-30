package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import ahchacha.ahchacha.domain.common.enums.PersonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reviewComment;

    //precision : 소수점을 포함한 전체 자릿수 / scale : 소수점 이하의 자릿수
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal reviewScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonType personType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

}
