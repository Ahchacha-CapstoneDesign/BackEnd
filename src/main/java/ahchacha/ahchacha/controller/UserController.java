package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.AuthenticationValue;
import ahchacha.ahchacha.domain.common.enums.PersonOrOfficial;
import ahchacha.ahchacha.dto.UserDto;
import ahchacha.ahchacha.repository.UserRepository;
import ahchacha.ahchacha.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto.LoginRequestDto loginRequestDto, HttpSession session) {
        try {
            User user = userService.login(loginRequestDto, session);

            // 사용자의 로그인 권한을 검사하고 personOrOfficial 값을 업데이트
//            if (loginRequestDto.getPersonOrOfficial() == PersonOrOfficial.OFFICIAL) {
//                if (user.getAuthenticationValue() == AuthenticationValue.CANOFFICIAL) {
//                    user.setPersonOrOfficial(PersonOrOfficial.OFFICIAL);
//                } else {
//                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("공식 사용자 인증이 필요합니다.");
//                }
//            } else {
//                user.setPersonOrOfficial(PersonOrOfficial.PERSON);
//            }
            if (loginRequestDto.getPersonOrOfficial() == PersonOrOfficial.OFFICIAL) {
                if (user.getAuthenticationValue() == AuthenticationValue.CANOFFICIAL || user.getAuthenticationValue() == AuthenticationValue.CANADMIN) {
                    user.setPersonOrOfficial(PersonOrOfficial.OFFICIAL);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("공식 사용자 인증이 필요합니다.");
                }
            }
            else if (loginRequestDto.getPersonOrOfficial() == PersonOrOfficial.ADMIN) {
                if (user.getAuthenticationValue() == AuthenticationValue.CANADMIN) {
                    user.setPersonOrOfficial(PersonOrOfficial.ADMIN);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 인증이 필요합니다.");
                }
            }
            else if (loginRequestDto.getPersonOrOfficial() == PersonOrOfficial.PERSON) {
                user.setPersonOrOfficial(PersonOrOfficial.PERSON);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 권한 요청입니다.");
            }

            userRepository.save(user); // 변경된 사용자 정보를 저장
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        try {
            userService.logout(session);
            return new ResponseEntity<>("Logout success", HttpStatus.OK);  // 로그아웃이 성공적으로 이루어지면 200 OK와 함께 메시지를 반환합니다.
        } catch (IOException e) {
            throw new RuntimeException("Logout failed", e);  // 로그아웃 실패 시, RuntimeException을 던집니다.
        }
    }


    @Operation(summary = "닉네임 설정")
    @PostMapping("/nickname")
    public ResponseEntity<String> setNickname(@RequestParam String nickname, HttpSession session) {
        try {
            userService.setNickname(nickname, session);
            return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
        } catch (IllegalStateException e) {
            // 여기에서는 사용자 정의 예외 메시지를 반환합니다.
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 그 외 모든 예외에 대해서는 더 일반적인 오류 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("닉네임 변경 중 예상치 못한 오류가 발생하였습니다.");
        }
    }


    @Operation(summary = "사용자 타입 변경 person or official")
    @PostMapping("/setUserType")
    public String setUserType(@RequestParam("userType") PersonOrOfficial userType, HttpSession session) {
        try {
            userService.setUserType(userType, session);
            return "사용자 타입이 성공적으로 업데이트 되었습니다.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    @Operation(summary = "현재 사용자 비밀번호 검증 코드")
    @PostMapping("/validatePassword")
    public ResponseEntity<?> validatePassword(@RequestBody String password, HttpSession session) {
        try {
            boolean isValid = userService.validateCurrentPassword(password, session);
            if (isValid) {
                return ResponseEntity.ok().body("비밀번호 검증에 성공했습니다.");
            } else {
                return ResponseEntity.badRequest().body("비밀번호가 올바르지 않습니다.");
            }
        } catch (IllegalStateException e) {
            // 예외 발생 시, 예외 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "설정 > 프로필 등록")
    @PostMapping(value = "/default-profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveProfile(@RequestPart(value = "file", required = false) MultipartFile file, HttpSession session) {

        String userProfile = userService.saveProfile(file, session, "defaultProfile");
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @Operation(summary = "설정 > 기본 프로필 사진으로 변경 ")
    @PostMapping("/reset-profile")
    public ResponseEntity<String> resetProfile(HttpSession session){
        User user = (User) session.getAttribute("user");

        userService.resetProfile(session,"defaultProfile");
        return ResponseEntity.ok("프로필이 기본상태로 변경되었습니다.");
    }

    @Operation(summary = "설정 > 프로필 조회")
    @GetMapping(value = "/default-profile")
    public ResponseEntity<String> getProfile(HttpSession session) {

        String userProfile = userService.getProfile(session);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

}