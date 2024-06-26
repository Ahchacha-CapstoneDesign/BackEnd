package ahchacha.ahchacha.controller;

import ahchacha.ahchacha.domain.User;
import ahchacha.ahchacha.domain.common.enums.AuthenticationValue;
import ahchacha.ahchacha.dto.AuthenticationDto;
import ahchacha.ahchacha.dto.ItemDto;
import ahchacha.ahchacha.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "OFFICIAL 인증 사진 및 소속 이름 제출")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AuthenticationDto.AuthenticationResponseDto> create(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                              @RequestParam(name = "officialName") String officialName,
                                                                              HttpSession session){

        AuthenticationDto.AuthenticationRequestDto authenticationRequestDto = AuthenticationDto.AuthenticationRequestDto.builder()
                .officialName(officialName)
                .build();

        AuthenticationDto.AuthenticationResponseDto authenticationResponseDto = authenticationService.createAuthentication(authenticationRequestDto, files, session);
        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "제출된 인증사진 리스트")
    @GetMapping("/allAuthentication")
    public ResponseEntity<Page<AuthenticationDto.AuthenticationResponseDto>> getAllAuthentication(@RequestParam(value = "page", defaultValue = "1")int page, HttpSession session) throws IllegalAccessException {
        Page<AuthenticationDto.AuthenticationResponseDto> authenticationResponseDtoPage = authenticationService.getAllAuthentication(page, session);
        return new ResponseEntity<>(authenticationResponseDtoPage, HttpStatus.OK);
    }

    @Operation(summary = "사용자의 인증 값을 변경")
    @PostMapping("/updateAuthenticationValue/{userId}")
    public ResponseEntity<Void> updateAuthenticationValue(@PathVariable Long userId, @RequestParam AuthenticationValue authenticationValue, HttpSession session) {
        try {
            authenticationService.updateAuthenticationValue(userId, authenticationValue, session);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "제출된 인증내역 상세조회")
    @GetMapping("/{authenticationId}")
    public ResponseEntity<AuthenticationDto.AuthenticationResponseDto> getAuthenticationById(@PathVariable Long authenticationId, HttpSession session) throws IllegalAccessException {
        Optional<AuthenticationDto.AuthenticationResponseDto> optionalAuthenticationDto = authenticationService.getAuthenticationById(authenticationId, session);

        return optionalAuthenticationDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> getAuthenticationStatus(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return authenticationService.getLatestAuthenticationByUserId(user.getId())
                .map(authentication -> ResponseEntity.ok(authentication.getIsCheck()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
