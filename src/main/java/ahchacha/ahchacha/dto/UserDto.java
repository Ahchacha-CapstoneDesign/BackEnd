package ahchacha.ahchacha.dto;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
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
        private PersonOrOfficial personOrOfficial;  // 로그인 유형을 위한 필드 추가
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
        private String defaultProfile;
        private PersonOrOfficial personOrOfficial;  // 응답에 로그인 유형 포함
        private String kakaoUrl;

        public static UserResponseDto toDto(User user) {
            return UserResponseDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .track1(user.getTrack1())
                    .track2(user.getTrack2())
                    .defaultProfile(user.getDefaultProfile())
                    .personOrOfficial(user.getPersonOrOfficial())
                    .build();
        }

    }
}