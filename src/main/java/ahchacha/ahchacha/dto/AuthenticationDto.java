package ahchacha.ahchacha.dto;

import ahchacha.ahchacha.domain.Authentication;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class AuthenticationDto {
    @Getter
    @Setter
    @Builder
    public static class AuthenticationRequestDto{
        private List<String> authenticationImageUrls;
        private String officialName;
    }

    @Getter
    @Setter
    @Builder
    public static class AuthenticationResponseDto{
        private Long userId;
        private Long id;
        private List<String> authenticationImageUrls;
        private String officialName;

        private String name;
        private String track1;
        private String track2;
        private String phoneNumber;
        private String grade;
        private String status;

        private Boolean isCheck;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static AuthenticationResponseDto toDto(Authentication authentication){
            return AuthenticationResponseDto.builder()
                    .userId(authentication.getUser().getId())
                    .id(authentication.getId())
                    .authenticationImageUrls(authentication.getAuthenticationImageUrls())
                    .officialName(authentication.getOfficialName())

                    .name(authentication.getUser().getName())
                    .track1(authentication.getUser().getTrack1())
                    .track2(authentication.getUser().getTrack2())
                    .phoneNumber(authentication.getUser().getPhoneNumber())
                    .grade(authentication.getUser().getGrade())
                    .status(authentication.getUser().getStatus())

                    .isCheck(authentication.getIsCheck())

                    .createdAt(authentication.getCreatedAt())
                    .updatedAt(authentication.getUpdatedAt())

                    .build();
        }
    }

    public static Page<AuthenticationResponseDto> toDtoPage(Page<Authentication> authenticationPage) {
        return authenticationPage.map(AuthenticationDto.AuthenticationResponseDto::toDto);
    }
}
