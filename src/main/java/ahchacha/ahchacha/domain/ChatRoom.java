package ahchacha.ahchacha.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomId;

    @NotNull
    private Long itemId;
    public ChatRoom(long itemId) {
        this.itemId = itemId;
    }
    public ChatRoom() {
    }
}
