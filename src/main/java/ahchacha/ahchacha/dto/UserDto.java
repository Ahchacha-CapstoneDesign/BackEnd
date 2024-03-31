package ahchacha.ahchacha.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Builder
    public static class UserRequestDto {
        private String nickname;
        private String track1;
        private String track2;
    }

    @Getter
    @Setter
    public static class LoginRequestDto {
        private String id;
        private String passwd;
    }

    @Getter
    @Setter
    @Builder
    public static class UserResponseDto {
        private Long id;
        private String nickname;
        private String name;
        private String track1;
        private String track2;

        public static UserDto.UserResponseDto toDto(Long id, String name, String track1, String track2) {
            return UserResponseDto.builder()
                    .id(id)
                    .name(name)
                    .track1(track1)
                    .track2(track2)
                    .build();
        }
    }
}