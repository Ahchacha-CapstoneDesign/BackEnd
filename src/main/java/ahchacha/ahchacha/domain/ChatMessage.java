package ahchacha.ahchacha.domain;

import ahchacha.ahchacha.domain.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage extends BaseEntity {

    public ChatMessage(String message) {
        this.type = MessageType.GREETING;
        this.message = message;
    }
    public enum MessageType {
        GREETING, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @NotNull
    private Long itemId;

    @ManyToOne @NotNull
    @JoinColumn(name = "user_id") //보내는 사람
    @JsonBackReference
    private User user;

    @NotNull
    private String message; //내용
}
