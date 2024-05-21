package ahchacha.ahchacha.service;

import ahchacha.ahchacha.aws.AmazonS3Manager;
import ahchacha.ahchacha.domain.Authentication;
import ahchacha.ahchacha.domain.Item;
import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.Uuid;
import ahchacha.ahchacha.domain.common.enums.AuthenticationValue;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import ahchacha.ahchacha.dto.AuthenticationDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.repository.AuthenticationRepository;
import ahchacha.ahchacha.repository.UserRepository;
import ahchacha.ahchacha.repository.UuidRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthenticationService {

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;
    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;

    @Transactional
    public AuthenticationDto.AuthenticationResponseDto createAuthentication(List<MultipartFile> files,
                                                                  HttpSession session) {
        User user = (User) session.getAttribute("user");

        //이미지 업로드
        List<String> pictureUrls = new ArrayList<>(); // 이미지 URL들을 저장할 리스트
        if (files != null && !files.isEmpty()){
            for (MultipartFile file : files) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());
                String pictureUrl = s3Manager.uploadFile(s3Manager.generateItemKeyName(savedUuid), file);
                pictureUrls.add(pictureUrl); // 리스트에 이미지 URL 추가

                System.out.println("s3 url(클릭 시 브라우저에 사진 뜨는지 확인): " + pictureUrl);
            }
        }

        Authentication authentication = Authentication.builder()
                .user(user)
                .name(user.getName())
                .track1(user.getTrack1())
                .track2(user.getTrack2())
                .phoneNumber(user.getPhoneNumber())
                .grade(user.getGrade())
                .status(user.getStatus())

                .authenticationImageUrls(pictureUrls)
                .isCheck(false)
                .build();

        Authentication createdAuthentication = authenticationRepository.save(authentication);
        return AuthenticationDto.AuthenticationResponseDto.toDto(createdAuthentication);
    }

    @Transactional
    public Page<AuthenticationDto.AuthenticationResponseDto> getAllAuthentication(int page, HttpSession session) throws IllegalAccessException {
        User user = (User) session.getAttribute("user");
        if (user.getPersonOrOfficial() != PersonOrOfficial.ADMIN) {
            throw new IllegalAccessException("관리자만 접근 가능합니다.");
        }

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page-1, 1000, Sort.by(sorts));
        Page<Authentication> authenticationPage = authenticationRepository.findAll(pageable);
        return AuthenticationDto.toDtoPage(authenticationPage);
    }

    @Transactional
    public void updateAuthenticationValue(Long userId, AuthenticationValue authenticationValue, HttpSession session) throws IllegalAccessException {
        User admin = (User) session.getAttribute("user");
        if (admin.getPersonOrOfficial() != PersonOrOfficial.ADMIN) {
            throw new IllegalAccessException("관리자만 접근 가능합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setAuthenticationValue(authenticationValue);
        userRepository.save(user);

        List<Authentication> authentications = authenticationRepository.findByUserId(userId);
        if (authentications.isEmpty()) {
            throw new IllegalArgumentException("인증 정보를 찾을 수 없습니다.");
        }

        for (Authentication authentication : authentications) {
            authentication.setIsCheck(true);
        }

        authenticationRepository.saveAll(authentications);
    }

    @Transactional
    public Optional<AuthenticationDto.AuthenticationResponseDto> getAuthenticationById(Long id, HttpSession session) throws IllegalAccessException {
        User admin = (User) session.getAttribute("user");
        if (admin.getPersonOrOfficial() != PersonOrOfficial.ADMIN) {
            throw new IllegalAccessException("관리자만 접근 가능합니다.");
        }

        Optional<Authentication> optionalAuthentication = authenticationRepository.findById(id);

        return optionalAuthentication.map(AuthenticationDto.AuthenticationResponseDto::toDto);
    }
}
