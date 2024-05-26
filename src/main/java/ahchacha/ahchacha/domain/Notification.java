package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import ahchacha.ahchacha.domain.common.enums.NotificationType;
import ahchacha.ahchacha.domain.common.enums.Reservation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Heart heart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservations_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservations reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Review review;

    private boolean isRead;
}
